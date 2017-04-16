package org.tio.server;

import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ReadCompletionHandler;
import org.tio.core.intf.Packet;
import org.tio.core.utils.SystemTimer;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 * 2017年4月4日 上午9:27:45
 */
public class AcceptCompletionHandler<SessionContext, P extends Packet, R> implements CompletionHandler<AsynchronousSocketChannel, AioServer<SessionContext, P, R>>
{

	private static Logger log = LoggerFactory.getLogger(AioServer.class);
	public AcceptCompletionHandler()
	{}
	/**
	 * 
	 * @param asynchronousSocketChannel
	 * @param aioServer
	 * @author: tanyaowu
	 */
	@Override
	public void completed(AsynchronousSocketChannel asynchronousSocketChannel, AioServer<SessionContext, P, R> aioServer)
	{
		try
		{
			ServerGroupContext<SessionContext, P, R> serverGroupContext = aioServer.getServerGroupContext();
			ServerGroupStat serverGroupStat = serverGroupContext.getServerGroupStat();
			serverGroupStat.getAccepted().incrementAndGet();

			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 32 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 32 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

			ServerChannelContext<SessionContext, P, R> channelContext = new ServerChannelContext<>(serverGroupContext, asynchronousSocketChannel);
			channelContext.setClosed(false);
			channelContext.setServerNode(aioServer.getServerNode());
			ServerAioListener<SessionContext, P, R> serverAioListener = serverGroupContext.getServerAioListener();
			channelContext.getStat().setTimeFirstConnected(SystemTimer.currentTimeMillis());
			try
			{
				serverAioListener.onAfterConnected(channelContext, true, false);
			} catch (Exception e)
			{
				log.error(e.toString(), e);
			}

			if (!aioServer.isWaitingStop())
			{
				ReadCompletionHandler<SessionContext, P, R> readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.getGroupContext().getReadBufferSize());
				readByteBuffer.position(0);
				readByteBuffer.limit(readByteBuffer.capacity());
				asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);
			}
		} catch (Exception e)
		{
			log.error("", e);
		} finally
		{
			if (aioServer.isWaitingStop())
			{
				log.info("{}即将关闭服务器，不再接受新请求", aioServer.getServerNode());
			} else
			{
				AsynchronousServerSocketChannel serverSocketChannel = aioServer.getServerSocketChannel();
				serverSocketChannel.accept(aioServer, this);
			}
		}
	}

	/**
	 * 
	 * @param exc
	 * @param aioServer
	 * @author: tanyaowu
	 */
	@Override
	public void failed(Throwable exc, AioServer<SessionContext, P, R> aioServer)
	{
		AsynchronousServerSocketChannel serverSocketChannel = aioServer.getServerSocketChannel();
		serverSocketChannel.accept(aioServer, this);

		log.error("[" + aioServer.getServerNode() + "]监听出现异常", exc);

	}

}
