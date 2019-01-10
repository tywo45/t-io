package org.tio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.GroupContext;
import org.tio.core.Node;
import org.tio.core.ReadCompletionHandler;
import org.tio.core.Tio;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.core.ssl.SslUtils;
import org.tio.core.stat.IpStat;
import org.tio.utils.SystemTimer;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:32:10
 */
public class ConnectionCompletionHandler implements CompletionHandler<Void, ConnectionCompletionVo> {
	private static Logger log = LoggerFactory.getLogger(ConnectionCompletionHandler.class);

	/**
	 *
	 * @param result
	 * @param attachment
	 * @author tanyaowu
	 */
	@Override
	public void completed(Void result, ConnectionCompletionVo attachment) {
		handler(result, attachment, null);
	}

	/**
	 *
	 * @param throwable
	 * @param attachment
	 * @author tanyaowu
	 */
	@Override
	public void failed(Throwable throwable, ConnectionCompletionVo attachment) {
		handler(null, attachment, throwable);
	}

	/**
	 *
	 * @param result
	 * @param attachment
	 * @param throwable
	 * @author tanyaowu
	 */
	private void handler(Void result, ConnectionCompletionVo attachment, Throwable throwable) {
		ClientChannelContext channelContext = attachment.getChannelContext();
		AsynchronousSocketChannel asynchronousSocketChannel = attachment.getAsynchronousSocketChannel();
		TioClient tioClient = attachment.getTioClient();
		ClientGroupContext clientGroupContext = tioClient.getClientGroupContext();
		Node serverNode = attachment.getServerNode();
		String bindIp = attachment.getBindIp();
		Integer bindPort = attachment.getBindPort();
		ClientAioListener clientAioListener = clientGroupContext.getClientAioListener();
		boolean isReconnect = attachment.isReconnect();
		boolean isConnected = false;

		try {
			if (throwable == null) {
				if (isReconnect) {
					channelContext.setAsynchronousSocketChannel(asynchronousSocketChannel);
					//				channelContext.getDecodeRunnable().setCanceled(false);
					channelContext.handlerRunnable.setCanceled(false);
					//		channelContext.getHandlerRunnableHighPrior().setCanceled(false);
					channelContext.sendRunnable.setCanceled(false);
					//		channelContext.getSendRunnableHighPrior().setCanceled(false);

					clientGroupContext.closeds.remove(channelContext);
				} else {
					channelContext = new ClientChannelContext(clientGroupContext, asynchronousSocketChannel);
					channelContext.setServerNode(serverNode);
				}

				channelContext.setBindIp(bindIp);
				channelContext.setBindPort(bindPort);

				channelContext.getReconnCount().set(0);
				channelContext.setClosed(false);
				isConnected = true;

				attachment.setChannelContext(channelContext);

				//				clientGroupContext.ips.bind(channelContext);
				clientGroupContext.connecteds.add(channelContext);

				ReadCompletionHandler readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.groupContext.getReadBufferSize());
				readByteBuffer.position(0);
				readByteBuffer.limit(readByteBuffer.capacity());
				asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);

				log.info("connected to {}", serverNode);
				if (isConnected && !isReconnect) {
					channelContext.stat.setTimeFirstConnected(SystemTimer.currTime);
				}
			} else {
				log.error(throwable.toString(), throwable);
				if (channelContext == null) {
					ReconnConf reconnConf = clientGroupContext.getReconnConf();
					if (reconnConf != null) {
						channelContext = new ClientChannelContext(clientGroupContext, asynchronousSocketChannel);
						channelContext.setServerNode(serverNode);
					}
				}

				if (!isReconnect) //不是重连，则是第一次连接
				{
					if (channelContext != null) {
						attachment.setChannelContext(channelContext);
					}
				}
				boolean f = ReconnConf.put(channelContext);
				if (!f) {
					Tio.close(channelContext, null, "不需要重连，关闭该连接", true, false);
				}
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			if (attachment.getCountDownLatch() != null) {
				attachment.getCountDownLatch().countDown();
			}

			try {
				if (channelContext != null) {
					channelContext.setReconnect(isReconnect);

					if (SslUtils.isSsl(channelContext.groupContext)) {
						if (isConnected) {
							//						channelContext.sslFacadeContext.beginHandshake();
							SslFacadeContext sslFacadeContext = new SslFacadeContext(channelContext);
							sslFacadeContext.beginHandshake();
						} else {
							if (clientAioListener != null) {
								clientAioListener.onAfterConnected(channelContext, isConnected, isReconnect);
							}
						}
					} else {
						if (clientAioListener != null) {
							clientAioListener.onAfterConnected(channelContext, isConnected, isReconnect);
						}
					}

					GroupContext groupContext = channelContext.groupContext;
					if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
						try {
							for (Long v : groupContext.ipStats.durationList) {
								IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
								ipStat.getRequestCount().incrementAndGet();
								groupContext.getIpStatListener().onAfterConnected(channelContext, isConnected, isReconnect, ipStat);
							}
						} catch (Exception e) {
							log.error(e.toString(), e);
						}
					}
				}
			} catch (Throwable e1) {
				log.error(e1.toString(), e1);
			}
		}
	}
}
