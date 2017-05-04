package org.tio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.ReadCompletionHandler;
import org.tio.core.intf.Packet;
import org.tio.core.utils.SystemTimer;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:32:10
 */
public class ConnectionCompletionHandler<SessionContext, P extends Packet, R> implements CompletionHandler<Void, ConnectionCompletionVo<SessionContext, P, R>> {
	private static Logger log = LoggerFactory.getLogger(ConnectionCompletionHandler.class);

	/**
	 * 
	 * @param result
	 * @param attachment
	 * @author: tanyaowu
	 */
	@Override
	public void completed(Void result, ConnectionCompletionVo<SessionContext, P, R> attachment) {
		handler(result, attachment, null);
	}

	/**
	 * 
	 * @param throwable
	 * @param attachment
	 * @author: tanyaowu
	 */
	@Override
	public void failed(Throwable throwable, ConnectionCompletionVo<SessionContext, P, R> attachment) {
		handler(null, attachment, throwable);
	}

	/**
	 * 
	 * @param result
	 * @param attachment
	 * @param throwable
	 * @author: tanyaowu
	 */
	private void handler(Void result, ConnectionCompletionVo<SessionContext, P, R> attachment, Throwable throwable) {
		ClientChannelContext<SessionContext, P, R> channelContext = attachment.getChannelContext();
		AsynchronousSocketChannel asynchronousSocketChannel = attachment.getAsynchronousSocketChannel();
		AioClient<SessionContext, P, R> aioClient = attachment.getAioClient();
		ClientGroupContext<SessionContext, P, R> clientGroupContext = aioClient.getClientGroupContext();
		Node serverNode = attachment.getServerNode();
		String bindIp = attachment.getBindIp();
		Integer bindPort = attachment.getBindPort();
		ClientAioListener<SessionContext, P, R> clientAioListener = clientGroupContext.getClientAioListener();
		boolean isReconnect = attachment.isReconnect();
		boolean isConnected = false;

		try {
			if (throwable == null) {
				if (isReconnect) {
					channelContext.setAsynchronousSocketChannel(asynchronousSocketChannel);
					//				channelContext.getDecodeRunnable().setCanceled(false);
					channelContext.getHandlerRunnable().setCanceled(false);
					//		channelContext.getHandlerRunnableHighPrior().setCanceled(false);
					channelContext.getSendRunnable().setCanceled(false);
					//		channelContext.getSendRunnableHighPrior().setCanceled(false);

					clientGroupContext.closeds.remove(channelContext);
				} else {
					channelContext = new ClientChannelContext<>(clientGroupContext, asynchronousSocketChannel);
					channelContext.setServerNode(serverNode);
					channelContext.getStat().setTimeClosed(SystemTimer.currentTimeMillis());
				}

				channelContext.setBindIp(bindIp);
				channelContext.setBindPort(bindPort);

				channelContext.setReconnCount(0);
				channelContext.setClosed(false);
				isConnected = true;

				attachment.setChannelContext(channelContext);

				clientGroupContext.connecteds.add(channelContext);

				ReadCompletionHandler<SessionContext, P, R> readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.getGroupContext().getReadBufferSize());
				readByteBuffer.position(0);
				readByteBuffer.limit(readByteBuffer.capacity());
				asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);

				log.info("connected to {}", serverNode);
				if (isConnected && !isReconnect) {
					channelContext.getStat().setTimeFirstConnected(SystemTimer.currentTimeMillis());
				}
			} else {
				log.error(throwable.toString(), throwable);
				if (channelContext == null) {
					channelContext = new ClientChannelContext<>(clientGroupContext, asynchronousSocketChannel);
					channelContext.setServerNode(serverNode);
					channelContext.getStat().setTimeClosed(SystemTimer.currentTimeMillis());
				}

				if (!isReconnect) //不是重连，则是第一次连接，需要把channelContext加到closeds行列
				{
					clientGroupContext.closeds.add(channelContext);
				}

				attachment.setChannelContext(channelContext);

				ReconnConf.put(channelContext);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			if (attachment.getCountDownLatch() != null) {
				attachment.getCountDownLatch().countDown();
			}

			try {
				clientAioListener.onAfterConnected(channelContext, isConnected, isReconnect);
			} catch (Exception e1) {
				log.error(e1.toString(), e1);
			}
		}
	}
}
