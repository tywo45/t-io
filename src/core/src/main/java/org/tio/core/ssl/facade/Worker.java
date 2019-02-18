package org.tio.core.ssl.facade;

import java.nio.ByteBuffer;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ssl.SslVo;
import org.tio.utils.hutool.StrUtil;

class Worker {

	private static Logger log = LoggerFactory.getLogger(Worker.class);

	/*  Uses the SSLEngine and Buffers to perform wrap/unwrap operations.
	 Also, provides access to SSLEngine ops for handshake
	 */
	private final static String		TAG						= "Worker";
	private final SSLEngine			_engine;
	private final Buffers			_buffers;
	private ISSLListener			_sslListener;
	private ISessionClosedListener	_sessionClosedListener	= new DefaultOnCloseListener();
	@SuppressWarnings("unused")
	private String					who;

	private ChannelContext channelContext;

	Worker(final String debugTag, SSLEngine engine, Buffers buffers, ChannelContext channelContext) {
		_engine = engine;
		_buffers = buffers;
		this.channelContext = channelContext;
		this.who = "[Worker:" + debugTag + "]";
	}

	@SuppressWarnings("unused")
	private void debug(final String msg, final String... args) {
		SSLLog.debug(TAG, msg, args);
	}

	void setSessionClosedListener(final ISessionClosedListener scl) {
		_sessionClosedListener = scl;
	}

	void beginHandshake() throws SSLException {
		_engine.beginHandshake();
	}

	SSLEngineResult.HandshakeStatus getHandshakeStatus() {
		return _engine.getHandshakeStatus();
	}

	/**
	 * 只是简单地调一下SSLEngine.getDelegatedTask()
	 * @return
	 */
	Runnable getDelegatedTask() {
		return _engine.getDelegatedTask();
	}

	/**
	 * 加密
	 * @param sslVo
	 * @param plainData
	 * @return
	 * @throws SSLException
	 */
	SSLEngineResult wrap(SslVo sslVo, ByteBuffer plainData) throws SSLException {
		_buffers.prepareForWrap(plainData);
		SSLEngineResult result = doWrap();

		emitWrappedData(sslVo, result);

		switch (result.getStatus()) {
		case BUFFER_UNDERFLOW:
			throw new RuntimeException("BUFFER_UNDERFLOW while wrapping!");
		case BUFFER_OVERFLOW:
			_buffers.grow(BufferType.OUT_CIPHER);
			if (plainData != null && plainData.hasRemaining()) {
				plainData.position(result.bytesConsumed());
				ByteBuffer remainingData = BufferUtils.slice(plainData);
				wrap(sslVo, remainingData);
			}
			break;
		case OK:
			break;
		case CLOSED:
			_sessionClosedListener.onSessionClosed();
			break;
		}
		return result;
	}

	/**
	 * 解密
	 * @param sslVo
	 * @param encryptedData 待解密的数据
	 * @return
	 * @throws SSLException
	 */
	SSLEngineResult unwrap(ByteBuffer encryptedData) throws SSLException {
		ByteBuffer allEncryptedData = _buffers.prependCached(encryptedData);
		_buffers.prepareForUnwrap(allEncryptedData);
		SSLEngineResult result = doUnwrap();
		allEncryptedData.position(result.bytesConsumed());
		ByteBuffer unprocessedEncryptedData = BufferUtils.slice(allEncryptedData);//未处理的数据

		emitPlainData(result);

		switch (result.getStatus()) {
		case BUFFER_UNDERFLOW: //数据不够解密不了，则把剩下的数据存起来，下次继续使用
			_buffers.cache(unprocessedEncryptedData);
			break;
		case BUFFER_OVERFLOW:
			_buffers.grow(BufferType.IN_PLAIN);
			if (unprocessedEncryptedData == null) {
				throw new RuntimeException("Worker.unwrap had " + "buffer_overflow but all data was consumed!!");
			} else {
				//				unwrap(sslVo, unprocessedEncryptedData);
				unwrap(unprocessedEncryptedData);
			}
			break;
		case OK:
			if (unprocessedEncryptedData == null) {
				_buffers.clearCache();
			} else {
				_buffers.cache(unprocessedEncryptedData);
			}
			break;
		case CLOSED:
			_sessionClosedListener.onSessionClosed();
			break;
		}
		if (_buffers.isCacheEmpty() == false && result.getStatus() == SSLEngineResult.Status.OK && result.bytesConsumed() > 0) {
			//			debug("Still data in cahce");
			ByteBuffer byteBuffer = ByteBuffer.allocate(0);
			result = unwrap(byteBuffer);
		}
		return result;
	}

	void setSSLListener(ISSLListener SSLListener) {
		_sslListener = SSLListener;
	}

	void handleEnOfSession(final SSLEngineResult result) {
		if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
			_sessionClosedListener.onSessionClosed();
		}
	}

	void close(boolean properly) {
		_engine.closeOutbound();
		try {
			if (properly) {
				wrap(new SslVo(), null); //sends a TLS close_notify alert
			}
			_engine.closeInbound();
		} catch (SSLException ignore) {
		}

	}

	boolean isCloseCompleted() {
		return _engine.isOutboundDone();
	}

	boolean pendingUnwrap() {
		return !_buffers.isCacheEmpty();
	}
	/* Private */

	private void emitWrappedData(SslVo sslVo, SSLEngineResult result) {
		if (result.bytesProduced() > 0) {
			ByteBuffer internalCipherBuffer = _buffers.get(BufferType.OUT_CIPHER);
			sslVo.setByteBuffer(makeExternalBuffer(internalCipherBuffer));
			_sslListener.onWrappedData(sslVo);
		}
	}

	private void emitPlainData(SSLEngineResult result) {
		if (result.bytesProduced() > 0) {
			ByteBuffer internalPlainBuffer = _buffers.get(BufferType.IN_PLAIN);
			ByteBuffer plainBuffer = (makeExternalBuffer(internalPlainBuffer));
			_sslListener.onPlainData(plainBuffer);
		}

	}

	/**
	 * 加密
	 * @return
	 * @throws SSLException
	 */
	private SSLEngineResult doWrap() throws SSLException {
		try {
			ByteBuffer plainText = _buffers.get(BufferType.OUT_PLAIN);
			ByteBuffer cipherText = _buffers.get(BufferType.OUT_CIPHER);
			if (log.isInfoEnabled()) {
				log.info("{}, doWrap(加密): plainText:{} to cipherText: {}", channelContext, plainText, cipherText);
			}
			return _engine.wrap(plainText, cipherText);
		} catch (SSLException e) {
			throw e;
		}
	}

	/**
	 * 解密
	 * @return
	 * @throws SSLException
	 */
	private SSLEngineResult doUnwrap() throws SSLException {
		ByteBuffer cipherText = _buffers.get(BufferType.IN_CIPHER);
		ByteBuffer plainText = _buffers.get(BufferType.IN_PLAIN);
		try {
			log.info("{}, doUnwrap(解密): 密文buffer:{}, 明文buffer: {}", channelContext, cipherText, plainText);
			return _engine.unwrap(cipherText, plainText);
		} catch (SSLException e) {
			if (log.isInfoEnabled()) {
				byte[] bs = new byte[cipherText.limit()];
				System.arraycopy(cipherText.array(), 0, bs, 0, bs.length);
				log.error(channelContext + ", 解密Error:" + e.toString() + ", byte:" + StrUtil.arrayToString(bs) + ", string:" + new String(bs) + ", buffer:" + cipherText, e);
			}
			throw e;
		}
	}

	private static ByteBuffer makeExternalBuffer(ByteBuffer internalBuffer) {
		ByteBuffer newBuffer = ByteBuffer.allocate(internalBuffer.position());
		internalBuffer.flip();
		BufferUtils.copy(internalBuffer, newBuffer);
		return newBuffer;
	}

}
