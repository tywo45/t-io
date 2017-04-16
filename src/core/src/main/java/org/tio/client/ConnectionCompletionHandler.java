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
public class ConnectionCompletionHandler<SessionContext, P extends Packet, R> implements CompletionHandler<Void, ConnectionCompletionVo<SessionContext, P, R> >
{
	private static Logger log = LoggerFactory.getLogger(ConnectionCompletionHandler.class);

	/** 
	 * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
	 * 
	 * @param result
	 * @param attachment
	 * @author: tanyaowu
	 * 2017年2月26日 下午9:39:18
	 * 
	 */
	@Override
	public void completed(Void result, ConnectionCompletionVo<SessionContext, P, R> attachment)
	{
		synchronized (attachment)
		{
			try
			{
				boolean isReconnect = attachment.isReconnect();
				ClientChannelContext<SessionContext, P, R> channelContext = attachment.getChannelContext();
				AsynchronousSocketChannel asynchronousSocketChannel = attachment.getAsynchronousSocketChannel();
				AioClient<SessionContext, P, R> aioClient = attachment.getAioClient();
				ClientGroupContext<SessionContext, P, R> clientGroupContext = aioClient.getClientGroupContext();
				Node serverNode = attachment.getServerNode();
				String bindIp = attachment.getBindIp();
				Integer bindPort = attachment.getBindPort();
				ClientAioListener<SessionContext, P, R> clientAioListener = clientGroupContext.getClientAioListener();

				if (isReconnect)
				{
					channelContext.setAsynchronousSocketChannel(asynchronousSocketChannel);
//					channelContext.getDecodeRunnable().setCanceled(false);
					channelContext.getHandlerRunnableNormPrior().setCanceled(false);
					//		channelContext.getHandlerRunnableHighPrior().setCanceled(false);
					channelContext.getSendRunnableNormPrior().setCanceled(false);
					//		channelContext.getSendRunnableHighPrior().setCanceled(false);

					clientGroupContext.getCloseds().remove(channelContext);
				} else
				{
					channelContext = new ClientChannelContext<>(clientGroupContext, asynchronousSocketChannel);
					channelContext.setServerNode(serverNode);
					channelContext.getStat().setTimeClosed(SystemTimer.currentTimeMillis());
				}

				channelContext.setBindIp(bindIp);
				channelContext.setBindPort(bindPort);

				channelContext.setReconnCount(0);
				channelContext.setClosed(false);
				
				attachment.setChannelContext(channelContext);

				clientGroupContext.getConnecteds().add(channelContext);

				ReadCompletionHandler<SessionContext, P, R> readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.getGroupContext().getReadBufferSize());
				readByteBuffer.position(0);
				readByteBuffer.limit(readByteBuffer.capacity());
				asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);

				boolean isConnected = !channelContext.isClosed();
				log.info("connected to {}", serverNode);
				if (isConnected && !isReconnect)
				{
					channelContext.getStat().setTimeFirstConnected(SystemTimer.currentTimeMillis());
				}
				try
				{
					clientAioListener.onAfterConnected(channelContext, isConnected, isReconnect);
				} catch (Exception e1)
				{
					log.error(e1.toString(), e1);
				}
			} catch (Exception e)
			{
				log.error(e.toString(), e);
			}
			
			attachment.notify();
		}
		
		
	}

	/** 
	 * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
	 * 
	 * @param exc
	 * @param attachment
	 * @author: tanyaowu
	 * 2017年2月26日 下午9:39:18
	 * 
	 */
	@Override
	public void failed(Throwable e, ConnectionCompletionVo<SessionContext, P, R> attachment)
	{
		synchronized (attachment)
		{
			ClientChannelContext<SessionContext, P, R> channelContext = null;
			ClientGroupContext<SessionContext, P, R> clientGroupContext = null;
			ClientAioListener<SessionContext, P, R> clientAioListener = null;
			try
			{
				log.error(e.toString(), e);

				boolean isReconnect = attachment.isReconnect();
				channelContext = attachment.getChannelContext();
				AsynchronousSocketChannel asynchronousSocketChannel = attachment.getAsynchronousSocketChannel();
				AioClient<SessionContext, P, R> aioClient = attachment.getAioClient();
				clientGroupContext = aioClient.getClientGroupContext();
				Node serverNode = attachment.getServerNode();
				clientAioListener = clientGroupContext.getClientAioListener();

				if (channelContext == null)
				{
					channelContext = new ClientChannelContext<>(clientGroupContext, asynchronousSocketChannel);
					channelContext.setServerNode(serverNode);
					channelContext.getStat().setTimeClosed(SystemTimer.currentTimeMillis());
				}

				if (!isReconnect) //不是重连，则是第一次连接，需要把channelContext加到closeds行列
				{
					clientGroupContext.getCloseds().add(channelContext);
				}
				
				attachment.setChannelContext(channelContext);
				
				try
				{
					clientAioListener.onAfterConnected(channelContext, !channelContext.isClosed(), isReconnect);
				} catch (Exception e1)
				{
					log.error(e1.toString(), e1);
				}
			} catch (Exception e1)
			{
				log.error(e1.toString(), e1);
			} finally
			{
				ReconnConf.put(channelContext);
				attachment.notify();
			}
		}
		

	}

}
