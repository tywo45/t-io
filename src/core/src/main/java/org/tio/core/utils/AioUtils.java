package org.tio.core.utils;

import java.nio.channels.AsynchronousSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.core.task.HandlerRunnable;
import org.tio.core.task.SendRunnable;
import org.tio.core.threadpool.SynThreadPoolExecutor;
import org.tio.core.threadpool.intf.SynRunnableIntf;

public class AioUtils
{
	private static Logger log = LoggerFactory.getLogger(AioUtils.class);

	public static <SessionContext, P extends Packet, R> boolean checkBeforeIO(ChannelContext<SessionContext, P, R> channelContext)
	{
		if (channelContext == null)
		{
			log.error("channelContext is null, {}", ThreadUtils.stackTrace());
			return false;
		}

		boolean isClosed = channelContext.isClosed();
		boolean isRemoved = channelContext.isRemoved();

		AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
		Boolean isopen = null;
		if (asynchronousSocketChannel != null)
		{
			isopen = asynchronousSocketChannel.isOpen();

			if (isClosed || isRemoved)
			{
				if (isopen)
				{
					try
					{
						Aio.close(channelContext, "asynchronousSocketChannel is open, but channelContext isClosed: " + isClosed + ", isRemoved: " + isRemoved);
					} catch (Exception e)
					{
						log.error(e.toString(), e);
					}
				}
				log.info("{}, isopen:{}, isClosed:{}, isRemoved:{}", channelContext, isopen, channelContext.isClosed(), channelContext.isRemoved());
				return false;
			}
		} else
		{
			log.error("{}, 请检查此异常, asynchronousSocketChannel is null, isClosed:{}, isRemoved:{}, {} ", channelContext, channelContext.isClosed(), channelContext.isRemoved(),
					ThreadUtils.stackTrace());
			return false;
		}

		if (!isopen)
		{
			log.info("{}, 可能对方关闭了连接, isopen:{}, isClosed:{}, isRemoved:{}", channelContext, isopen, channelContext.isClosed(), channelContext.isRemoved());
			Aio.close(channelContext, "asynchronousSocketChannel is not open, 可能对方关闭了连接");
			return false;
		}
		return true;
	}

	public static <SessionContext, P extends Packet, R> SendRunnable<SessionContext, P, R> selectSendRunnable(ChannelContext<SessionContext, P, R> channelContext, Packet packet)
	{
//		byte priority = packet.getPriority();
//		if (priority == org.tio.core.intf.Packet.PRIORITY_HIGH)
//		{
//			return channelContext.getSendRunnableHighPrior();
//		} else
//		{
			return channelContext.getSendRunnableNormPrior();
//		}
	}

	public static <SessionContext, P extends Packet, R> SynThreadPoolExecutor<SynRunnableIntf> selectSendExecutor(ChannelContext<SessionContext, P, R> channelContext, Packet packet)
	{
//		byte priority = packet.getPriority();
//		if (priority == org.tio.core.intf.Packet.PRIORITY_HIGH)
//		{
//			return channelContext.getGroupContext().getSendExecutorHighPrior();
//		} else
//		{
			return channelContext.getGroupContext().getSendExecutorNormPrior();
//		}
	}

	public static <SessionContext, P extends Packet, R> HandlerRunnable<SessionContext, P, R> selectHandlerRunnable(ChannelContext<SessionContext, P, R> channelContext, Packet packet)
	{
//		byte priority = packet.getPriority();
//		if (priority == org.tio.core.intf.Packet.PRIORITY_HIGH)
//		{
//			return channelContext.getHandlerRunnableHighPrior();
//		} else
//		{
			return channelContext.getHandlerRunnableNormPrior();
//		}
	}

	public static <SessionContext, P extends Packet, R> SynThreadPoolExecutor<SynRunnableIntf> selectHandlerExecutor(ChannelContext<SessionContext, P, R> channelContext, Packet packet)
	{
//		byte priority = packet.getPriority();
//		if (priority == org.tio.core.intf.Packet.PRIORITY_HIGH)
//		{
//			return channelContext.getGroupContext().getHandlerExecutorHighPrior();
//		} else
//		{
			return channelContext.getGroupContext().getHandlerExecutorNormPrior();
//		}
	}

}
