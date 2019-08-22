package org.tio.core.task;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelContext.CloseCode;
import org.tio.core.TioConfig;
import org.tio.core.TcpConst;
import org.tio.core.Tio;
import org.tio.core.WriteCompletionHandler.WriteCompletionVo;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.SslUtils;
import org.tio.core.ssl.SslVo;
import org.tio.core.utils.TioUtils;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 上午9:19:18
 */
public class SendRunnable extends AbstractQueueRunnable<Packet> {
	private static final Logger				log									= LoggerFactory.getLogger(SendRunnable.class);
	private ChannelContext					channelContext						= null;
	private TioConfig					tioConfig						= null;
	private AioHandler						aioHandler							= null;
	private boolean							isSsl								= false;
	/** The msg queue. */
	private ConcurrentLinkedQueue<Packet>	forSendAfterSslHandshakeCompleted	= null;											//new ConcurrentLinkedQueue<>();

	public ConcurrentLinkedQueue<Packet> getForSendAfterSslHandshakeCompleted(boolean forceCreate) {
		if (forSendAfterSslHandshakeCompleted == null && forceCreate) {
			synchronized (this) {
				if (forSendAfterSslHandshakeCompleted == null) {
					forSendAfterSslHandshakeCompleted = new ConcurrentLinkedQueue<>();
				}
			}
		}

		return forSendAfterSslHandshakeCompleted;
	}

	//SSL加密锁
	//	private Object sslEncryptLock = new Object();

	/**
	 *
	 * @param channelContext
	 * @param executor
	 * @author tanyaowu
	 */
	public SendRunnable(ChannelContext channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
		this.tioConfig = channelContext.tioConfig;
		this.aioHandler = tioConfig.getAioHandler();
		this.isSsl = SslUtils.isSsl(tioConfig);
	}

	@Override
	public boolean addMsg(Packet packet) {
		if (this.isCanceled()) {
			log.info("{}, 任务已经取消，{}添加到发送队列失败", channelContext, packet.logstr());
			return false;
		}

		if (tioConfig.packetConverter != null) {
			Packet packet1 = tioConfig.packetConverter.convert(packet, channelContext);
			if (packet1 == null) {
				log.info("convert后为null，表示不需要发送", channelContext, packet.logstr());
				return true;
			}
			packet = packet1;
		}

		if (channelContext.sslFacadeContext != null && !channelContext.sslFacadeContext.isHandshakeCompleted() && SslUtils.needSslEncrypt(packet, tioConfig)) {
			return this.getForSendAfterSslHandshakeCompleted(true).add(packet);
		} else {
			return msgQueue.add(packet);
		}
	}

	/**
	 * 清空消息队列
	 */
	@Override
	public void clearMsgQueue() {
		Packet p = null;
		forSendAfterSslHandshakeCompleted = null;
		while ((p = msgQueue.poll()) != null) {
			try {
				channelContext.processAfterSent(p, false);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}
		}
	}

	private ByteBuffer getByteBuffer(Packet packet) {
		try {
			ByteBuffer byteBuffer = packet.getPreEncodedByteBuffer();
			if (byteBuffer != null) {
				//			byteBuffer = byteBuffer.duplicate();
			} else {
				byteBuffer = aioHandler.encode(packet, tioConfig, channelContext);
			}

			if (!byteBuffer.hasRemaining()) {
				byteBuffer.flip();
			}
			return byteBuffer;
		} catch (Exception e) {
			log.error(packet.logstr(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 新旧值是否进行了切换
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	private static boolean swithed(Boolean oldValue, boolean newValue) {
		if (oldValue == null) {
			return false;
		}

		if (Objects.equals(oldValue, newValue)) {
			return false;
		}

		return true;
	}

	private static final int MAX_CAPACITY = TcpConst.MAX_DATA_LENGTH - 1024; //减掉1024是尽量防止溢出的一小部分还分成一个tcp包发出 

	//	private int repeatCount = 0;

	@Override
	public void runTask() {
		int queueSize = msgQueue.size();
		if (queueSize == 0) {
			return;
		}

		if (queueSize == 1) {
			//			System.out.println(1);
			Packet packet = msgQueue.poll();
			if (packet != null) {
				sendPacket(packet);
			}
			return;
		}

		int listInitialCapacity = Math.min(queueSize, 200);

		Packet packet = null;
		List<Packet> packets = new ArrayList<>(listInitialCapacity);
		List<ByteBuffer> byteBuffers = new ArrayList<>(listInitialCapacity);
		//		int packetCount = 0;
		int allBytebufferCapacity = 0;
		Boolean needSslEncrypted = null;
		boolean sslSwitched = false;
		while ((packet = msgQueue.poll()) != null) {
			ByteBuffer byteBuffer = getByteBuffer(packet);

			packets.add(packet);
			byteBuffers.add(byteBuffer);
			//			packetCount++;
			allBytebufferCapacity += byteBuffer.limit();

			if (isSsl) {
				if (packet.isSslEncrypted()) {
					boolean _needSslEncrypted = false;
					sslSwitched = swithed(needSslEncrypted, _needSslEncrypted);
					needSslEncrypted = _needSslEncrypted;
				} else {
					boolean _needSslEncrypted = true;
					sslSwitched = swithed(needSslEncrypted, _needSslEncrypted);
					needSslEncrypted = _needSslEncrypted;
				}
			} else { //非ssl，不涉及到加密和不加密的切换
				needSslEncrypted = false;
			}

			if ((allBytebufferCapacity >= MAX_CAPACITY) || sslSwitched) {
				break;
			}
		}

		if (allBytebufferCapacity == 0) {
			return;
		}
		ByteBuffer allByteBuffer = ByteBuffer.allocate(allBytebufferCapacity);
		for (ByteBuffer byteBuffer : byteBuffers) {
			allByteBuffer.put(byteBuffer);
		}

		allByteBuffer.flip();

		if (needSslEncrypted) {
			SslVo sslVo = new SslVo(allByteBuffer, packets);
			try {
				channelContext.sslFacadeContext.getSslFacade().encrypt(sslVo);
				allByteBuffer = sslVo.getByteBuffer();
			} catch (SSLException e) {
				log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
				Tio.close(channelContext, "进行SSL加密时发生了异常", CloseCode.SSL_ENCRYPTION_ERROR);
				return;
			}
		}

		this.sendByteBuffer(allByteBuffer, packets);
		//		queueSize = msgQueue.size();
		//		if (queueSize > 0) {
		//			repeatCount++;
		//			if (repeatCount < 3) {
		//				runTask();
		//				return;
		//			}
		//		}
		//		repeatCount = 0;
	}

	public boolean sendPacket(Packet packet) {
		ByteBuffer byteBuffer = getByteBuffer(packet);

		if (isSsl) {
			if (!packet.isSslEncrypted()) {
				SslVo sslVo = new SslVo(byteBuffer, packet);
				try {
					channelContext.sslFacadeContext.getSslFacade().encrypt(sslVo);
					byteBuffer = sslVo.getByteBuffer();
				} catch (SSLException e) {
					log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
					Tio.close(channelContext, "进行SSL加密时发生了异常", CloseCode.SSL_ENCRYPTION_ERROR);
					return false;
				}
			}
		}

		sendByteBuffer(byteBuffer, packet);
		return true;
	}

	/**
	 *
	 * @param byteBuffer
	 * @param packets Packet or List<Packet>
	 * @author tanyaowu
	 */
	public void sendByteBuffer(ByteBuffer byteBuffer, Object packets) {
		if (byteBuffer == null) {
			log.error("{},byteBuffer is null", channelContext);
			return;
		}

		if (!TioUtils.checkBeforeIO(channelContext)) {
			return;
		}

		if (!byteBuffer.hasRemaining()) {
			byteBuffer.flip();
		}

		try {
			channelContext.writeCompletionHandler.getWriteSemaphore().acquire();
		} catch (InterruptedException e) {
			log.error(e.toString(), e);
		}

		write(byteBuffer, packets);
	}

	private void write(ByteBuffer byteBuffer, Object packets) {
		WriteCompletionVo writeCompletionVo = new WriteCompletionVo(byteBuffer, packets);
		channelContext.asynchronousSocketChannel.write(byteBuffer, writeCompletionVo, channelContext.writeCompletionHandler);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String logstr() {
		return toString();
	}

}
