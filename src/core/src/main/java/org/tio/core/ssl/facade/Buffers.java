package org.tio.core.ssl.facade;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

class Buffers {

	private static Logger log = LoggerFactory.getLogger(Buffers.class);

	/*
	 Buffers is a simple abstraction that encapsulates the 4 SSL
	 buffers and an unwrap caching buffer.
	
	 The unwrap caching buffer is required to cache previously received
	 partial TLS packet which could not be unwrapped.
	
	 The four ByteBuffers required to operate an SSLEngine. One way to look
	 at the role of these buffers is that two of these buffers are used to
	 process incoming data and the other two are used to process outgoing data.
	 Another way to look at this is to say that two buffers represent the
	 host application and two represent the peer application. The Java
	 SSLEngine documentation calls these buffers application and network
	 buffers and refers to them variously but most commonly as myAppData,
	 myNetData, peerAppData and peerNetData. I have used these same names for
	 the private fields in this class so that the reader is able to associate
	 them to the Java provided documentation with ease. For publically visible
	 contracts, I felt better names were possible and have defined them
	 in an enum called BufferType. Also, note that handshake data during an
	 unwrap is never put in the IN_PLAIN buffer after unwrapping,
	 only application data is available here when applicable.
	
	 In order to create an instance of Buffers all we need is a SSLSession.
	
	 These buffers should not be reused by the host application for any
	 other purpose as SSLEngine might modify the source buffer during an
	 unwrap. Additionally, it is important to note that these buffers may
	 have to be resized during operations and hence it is neither simple nor
	 maintainable to allow the host application to inject its own buffers.
	 In short, leave these buffers alone!
	 */
	private ByteBuffer				_peerApp;
	private ByteBuffer				_myApp;
	private ByteBuffer				_peerNet;
	private ByteBuffer				_myNet;
	/**
	 * 待解密的bytebuffer
	 */
	private final AppendableBuffer	waitUnwrapBuffer;
	/**
	 * 
	 */
	private final SSLSession		ssLSession;

	@SuppressWarnings("unused")
	private ChannelContext channelContext;

	public Buffers(SSLSession ssLSession, ChannelContext channelContext) {
		this.channelContext = channelContext;
		this.ssLSession = ssLSession;
		allocate();
		waitUnwrapBuffer = new AppendableBuffer();
	}

	//	private void debug(final String msg) {
	//		System.out.println("[Buffers]" + msg);
	//		System.out.flush();
	//	}

	ByteBuffer get(BufferType t) {
		ByteBuffer result = null;
		switch (t) {
		case IN_PLAIN:
			result = _peerApp;
			break;
		case IN_CIPHER:
			result = _peerNet;
			break;
		case OUT_PLAIN:
			result = _myApp;
			break;
		case OUT_CIPHER:
			result = _myNet;
			break;
		}
		return result;
	}

	void grow(BufferType t) {
		/* Grows buffer to recommended SSL sizes */
		switch (t) {
		case IN_PLAIN:
			assign(t, grow(t, ssLSession.getApplicationBufferSize()));
			break;
		case IN_CIPHER:
			assign(t, grow(t, ssLSession.getPacketBufferSize()));
			break;
		case OUT_PLAIN:
			//No known reason for this case to occur
			break;
		case OUT_CIPHER:
			assign(t, grow(t, ssLSession.getPacketBufferSize()));
			break;
		}

	}

	ByteBuffer grow(BufferType b, int recommendedBufferSize) {
		ByteBuffer originalBuffer = get(b);
		ByteBuffer newBuffer = ByteBuffer.allocate(recommendedBufferSize);

		try {
			//debug("grow buffer " + originalBuffer + " to " + newBuffer);
			BufferUtils.copy(originalBuffer, newBuffer);
		} catch (BufferOverflowException e) {
			throw e;
		}
		return newBuffer;
	}

	void prepareForUnwrap(ByteBuffer data) {
		clear(BufferType.IN_CIPHER, BufferType.IN_PLAIN);
		if (data != null) {
			try {
				ByteBuffer newBuffer = growIfNecessary(BufferType.IN_CIPHER, data.limit());
				newBuffer.put(data);
				newBuffer.flip();
			} catch (Exception e) {
				log.error(e.toString() + ", data: " + data + ", BufferType.IN_CIPHER:" + get(BufferType.IN_CIPHER), e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 
	 * @param plainData 待加密的ByteBuffer
	 */
	void prepareForWrap(ByteBuffer plainData) {
		//Avoid buffer overflow when loading plain data and clear buffers
		clear(BufferType.OUT_PLAIN, BufferType.OUT_CIPHER);
		if (plainData != null) {
			ByteBuffer newBuffer = growIfNecessary(BufferType.OUT_PLAIN, plainData.limit());
			newBuffer.put(plainData);
			newBuffer.flip();
		}
	}

	/* AppendableBuffer - Unwrap cache ops */
	ByteBuffer prependCached(ByteBuffer data) {
		if (data == null) {
			return waitUnwrapBuffer.get();
		} else {
			ByteBuffer result = waitUnwrapBuffer.append(data);
			result.rewind();
			return result;
		}
	}

	void cache(ByteBuffer data) {
		if (data != null) {
			waitUnwrapBuffer.set(data);
		}
	}

	void clearCache() {
		waitUnwrapBuffer.clear();
	}

	boolean isCacheEmpty() {
		return !waitUnwrapBuffer.hasRemaining();
	}

	/* private */
	private void allocate() {
		int applicationBufferSize = ssLSession.getApplicationBufferSize();
		int packetBufferSize = ssLSession.getPacketBufferSize();
		_peerApp = ByteBuffer.allocate(applicationBufferSize);
		_myApp = ByteBuffer.allocate(applicationBufferSize);
		_peerNet = ByteBuffer.allocate(packetBufferSize);
		_myNet = ByteBuffer.allocate(packetBufferSize);
	}

	private void clear(BufferType source, BufferType destination) {
		get(source).clear();
		get(destination).clear();
	}

	private void assign(BufferType t, ByteBuffer b) {
		switch (t) {

		case IN_PLAIN:
			_peerApp = b;
			break;
		case IN_CIPHER:
			_peerNet = b;
			break;
		case OUT_PLAIN:
			_myApp = b;
			break;
		case OUT_CIPHER:
			_myNet = b;
			break;
		}
	}

	private void resetSize(BufferType t, int size) {
		ByteBuffer newBuffer = ByteBuffer.allocate(size);
		try {
			BufferUtils.copy(get(t), newBuffer);
			assign(t, newBuffer);
			//			ByteBuffer ss = get(t);
			//			log.error("size:{}, newbytebuffer:{}", size, ss);
		} catch (BufferOverflowException e) {
			throw e;
		}
	}

	private ByteBuffer growIfNecessary(BufferType t, int size) {
		//grow if not enough space
		ByteBuffer b = get(t);
		//    log.info("growIfNecessary {}, size:{}", b, size);
		//  System.out.println("Grow " + t + " : " + b + " size=" + size);
		if (b.position() + size > b.capacity()) {
			//  System.out.println("Grow");
			resetSize(t, b.limit() + size);
		}
		return get(t);
	}
}
