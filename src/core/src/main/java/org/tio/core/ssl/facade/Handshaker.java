package org.tio.core.ssl.facade;

import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ssl.SslVo;

class Handshaker {
	/*
	 The purpose of this class is to conduct a SSL handshake. To do this it
	 requires a SSLEngine as a provider of SSL knowhow. Byte buffers that are
	 required by the SSLEngine to execute its wrap and unwrap methods. And a
	 ITaskHandler callback that is used to delegate the responsibility of
	 executing long-running/IO tasks to the host application. By providing a
	 ITaskHandler the host application gains the flexibility of executing
	 these tasks in compliance with its own compute/IO strategies.
	 */

	private static Logger log = LoggerFactory.getLogger(Handshaker.class);

	@SuppressWarnings("unused")
	private final static String			TAG	= "Handshaker";
	private final ITaskHandler			_taskHandler;
	private final Worker				_worker;
	private boolean						_finished;
	private IHandshakeCompletedListener	_hscl;
	private ISessionClosedListener		_sessionClosedListener;
	@SuppressWarnings("unused")
	private boolean						_client;
	private ChannelContext				channelContext;

	public Handshaker(boolean client, Worker worker, ITaskHandler taskHandler, ChannelContext channelContext) {
		this.channelContext = channelContext;
		_worker = worker;
		_taskHandler = taskHandler;
		_finished = false;
		_client = client;
	}

	private void debug(final String msg, final String... args) {
		SSLLog.debug(channelContext.toString(), msg, args);
	}

	void begin() throws SSLException {
		_worker.beginHandshake();
		shakehands();
	}

	void carryOn() throws SSLException {
		shakehands();
	}

	void handleUnwrapResult(SSLEngineResult result) throws SSLException {
		if (result.getHandshakeStatus().equals(SSLEngineResult.HandshakeStatus.FINISHED)) {
			handshakeFinished(); //客户端会走到这一行
		} else {
			shakehands();
		}
	}

	void addCompletedListener(IHandshakeCompletedListener hscl) {
		_hscl = hscl;
	}

	void removeCompletedListener(IHandshakeCompletedListener hscl) {
		_hscl = hscl;
	}

	boolean isFinished() {
		return _finished;
	}

	/**
	 * 
	 * @throws SSLException
	 */
	private void shakehands() throws SSLException {
		HandshakeStatus handshakeStatus = _worker.getHandshakeStatus();
		log.info("{}, handshakeStatus:{}", this.channelContext, handshakeStatus);
		switch (handshakeStatus) {
		case NOT_HANDSHAKING:
			/* Occurs after handshake is over 握手早就完成了 */
			break;
		case FINISHED: //握手刚刚完成
			handshakeFinished();
			break;
		case NEED_TASK: //运行任务
			_taskHandler.process(new Tasks(_worker, this));
			break;
		case NEED_WRAP: //加密
			SSLEngineResult w_result = _worker.wrap(new SslVo(), null);
			if (w_result.getStatus().equals(SSLEngineResult.Status.CLOSED) && null != _sessionClosedListener) {
				_sessionClosedListener.onSessionClosed();
			}
			if (w_result.getHandshakeStatus().equals(SSLEngineResult.HandshakeStatus.FINISHED)) {
				handshakeFinished();
			} else {
				shakehands();
			}
			break;
		case NEED_UNWRAP:
			if (_worker.pendingUnwrap()) {
				SSLEngineResult u_result = _worker.unwrap(null);
				debug("Unwrap result " + u_result);
				if (u_result.getHandshakeStatus().equals(SSLEngineResult.HandshakeStatus.FINISHED)) {
					handshakeFinished();
				}
				if (u_result.getStatus().equals(SSLEngineResult.Status.OK)) {
					shakehands();
				}
			} else {
				debug("No pending data to unwrap");
			}
			break;
		}
	}

	private void handshakeFinished() {
		_finished = true;
		_hscl.onComplete();
	}

}
