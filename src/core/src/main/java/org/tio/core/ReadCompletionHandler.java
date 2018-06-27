package org.tio.core;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.core.stat.GroupStat;
import org.tio.core.stat.IpStat;
import org.tio.core.utils.AioUtils;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.utils.SystemTimer;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 上午9:22:04
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
	private static Logger log = LoggerFactory.getLogger(ReadCompletionHandler.class);
	private ChannelContext channelContext = null;
	private ByteBuffer readByteBuffer;

	//	private ByteBuffer byteBuffer = ByteBuffer.allocate(ChannelContext.READ_BUFFER_SIZE);

	/**
	 *
	 * @param channelContext
	 * @author tanyaowu
	 */
	public ReadCompletionHandler(ChannelContext channelContext) {
		this.channelContext = channelContext;
		this.readByteBuffer = ByteBuffer.allocate(channelContext.groupContext.readBufferSize);
	}

	@Override
	public void completed(Integer result, ByteBuffer byteBuffer) {
		if (result > 0) {
			GroupContext groupContext = channelContext.groupContext;

			groupContext.groupStat.receivedBytes.addAndGet(result);
			groupContext.groupStat.receivedTcps.incrementAndGet();

			channelContext.stat.receivedBytes.addAndGet(result);
			channelContext.stat.receivedTcps.incrementAndGet();
			channelContext.stat.latestTimeOfReceivedByte = SystemTimer.currentTimeMillis();

			if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
				try {
					for (Long v : groupContext.ipStats.durationList) {
						IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
						ipStat.getReceivedBytes().addAndGet(result);
						ipStat.getReceivedTcps().incrementAndGet();
						groupContext.getIpStatListener().onAfterReceivedBytes(channelContext, result, ipStat);
					}
				} catch (Exception e1) {
					log.error(channelContext.toString(), e1);
				}
			}

			try {
				groupContext.getAioListener().onAfterReceivedBytes(channelContext, result);
			} catch (Exception e) {
				log.error("", e);
			}

			if (channelContext.isTraceClient) {
				Map<String, Object> map = new HashMap<>(10);
				map.put("p_r_buf_len", result);
				channelContext.traceClient(ChannelAction.RECEIVED_BUF, null, map);
			}

			SslFacadeContext sslFacadeContext = channelContext.sslFacadeContext;
			if (sslFacadeContext == null) {
				readByteBuffer.flip();
				channelContext.decodeRunnable.setNewByteBuffer(readByteBuffer);
				channelContext.decodeRunnable.run();
			} else {
				ByteBuffer copiedByteBuffer = null;
				try {
					copiedByteBuffer = ByteBufferUtils.copy(readByteBuffer, 0, readByteBuffer.position());
					log.debug("{}, 丢给SslFacade解密:{}", channelContext, copiedByteBuffer);
					sslFacadeContext.getSslFacade().decrypt(copiedByteBuffer);
				} catch (Exception e) {
					log.error(channelContext + ", " + e.toString() + copiedByteBuffer, e);
					Tio.close(channelContext, e, e.toString());
				}
			}
			//			decodeRunnable.addMsg(newByteBuffer);
			//			groupContext.getDecodeExecutor().execute(decodeRunnable);
		} else if (result == 0) {
			log.error("{}, 读到的数据长度为0", channelContext);
			Tio.close(channelContext, null, "读到的数据长度为0");
			return;
		} else if (result < 0) {
			if (result == -1) {
				Tio.close(channelContext, null, "对方关闭了连接");
				return;
			} else {
				Tio.close(channelContext, null, "读数据时返回" + result);
				return;
			}
		}

		if (AioUtils.checkBeforeIO(channelContext)) {
			readByteBuffer.position(0);
			readByteBuffer.limit(readByteBuffer.capacity());
			channelContext.asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, this);
		}
	}

	/**
	 *
	 * @param exc
	 * @param byteBuffer
	 * @author tanyaowu
	 */
	@Override
	public void failed(Throwable exc, ByteBuffer byteBuffer) {
		Tio.close(channelContext, exc, "读数据时发生异常");
	}

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public ByteBuffer getReadByteBuffer() {
		return readByteBuffer;
	}
}
