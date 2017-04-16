package org.tio.examples.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.CommandStat;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImServerAioListener implements ServerAioListener<ImSessionContext, ImPacket, Object>
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImServerAioListener.class);

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public ImServerAioListener()
	{
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public static void main(String[] args)
	{
	}

	/** 
	 * @see org.tio.server.intf.ServerAioListener#onAfterAccepted(java.nio.channels.AsynchronousSocketChannel, org.tio.server.AioServer)
	 * 
	 * @param asynchronousSocketChannel
	 * @param aioServer
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:03:45
	 * 
	 */
//	@Override
//	public boolean onAfterAccepted(AsynchronousSocketChannel asynchronousSocketChannel, AioServer<ImSessionContext, ImPacket, Object> aioServer)
//	{
//		return true;
//	}

	@Override
	public void onAfterConnected(ChannelContext<ImSessionContext, ImPacket, Object> channelContext, boolean isConnected, boolean isReconnect)
	{
		ImSessionContext imSessionContext = new ImSessionContext();
		channelContext.setSessionContext(imSessionContext);
		return;
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onBeforeSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:08:44
	 * 
	 */
	@Override
	public void onAfterSent(ChannelContext<ImSessionContext, ImPacket, Object> channelContext, ImPacket packet, boolean isSentSuccess)
	{
		if (isSentSuccess)
		{
			CommandStat.getCount(packet.getCommand()).sent.incrementAndGet();
		}
		

	}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterReceived(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:08:44
	 * 
	 */
	@Override
	public void onAfterReceived(ChannelContext<ImSessionContext, ImPacket, Object> channelContext, ImPacket packet, int packetSize)
	{
		CommandStat.getCount(packet.getCommand()).received.incrementAndGet();
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterClose(org.tio.core.ChannelContext, java.lang.Throwable, java.lang.String)
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author: tanyaowu
	 * 2017年2月1日 上午11:03:11
	 * 
	 */
	@Override
	public void onAfterClose(ChannelContext<ImSessionContext, ImPacket, Object> channelContext, Throwable throwable, String remark, boolean isRemove)
	{

	}

}
