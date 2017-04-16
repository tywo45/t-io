package org.tio.core;

import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.Packet;
import org.tio.core.intf.PacketWithSendMode;
import org.tio.core.stat.GroupStat;

/**
 * 
 * @author tanyaowu 
 *
 */
public class WriteCompletionHandler<SessionContext, P extends Packet, R> implements CompletionHandler<Integer, Object>
{

	private static Logger log = LoggerFactory.getLogger(WriteCompletionHandler.class);

	private ChannelContext<SessionContext, P, R> channelContext = null;

	private java.util.concurrent.Semaphore writeSemaphore = new Semaphore(1);

	public WriteCompletionHandler(ChannelContext<SessionContext, P, R> channelContext)
	{
		this.channelContext = channelContext;
	}

	@Override
	public void completed(Integer result, Object packets)
	{
		handle(result, null, packets);
	}

	@Override
	public void failed(Throwable throwable, Object packets)
	{
		handle(0, throwable, packets);
	}

	@SuppressWarnings("unchecked")
	public void handle(Integer result, Throwable throwable, Object packets)
	{
		this.writeSemaphore.release();

		//有可能会是null
		PacketWithSendMode packetWithSendMode = null;

		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		GroupStat groupStat = groupContext.getGroupStat();
		AioListener<SessionContext, P, R> aioListener = groupContext.getAioListener();
		boolean isSentSuccess = (result > 0);

		if (isSentSuccess)
		{
			groupStat.getSentBytes().addAndGet(result);
		}

		int packetCount = 0;
		try
		{
			boolean isPacket = packets instanceof Packet;
			boolean isPacketWithSendModel = packets instanceof PacketWithSendMode;
			if (isPacket || isPacketWithSendModel)
			{
				if (isSentSuccess)
				{
					packetCount = 1;
					groupStat.getSentPacket().addAndGet(packetCount);
				}

				P packet = null;
				if (isPacket)
				{
					//					log.error("isPacket : true");
					packet = (P) packets;
				} else
				{
					packetWithSendMode = (PacketWithSendMode) packets;
					packet = (P) packetWithSendMode.getPacket();
					packetWithSendMode.setIsSentSuccess(isSentSuccess);
					//					log.error("{},发送成功:{}", channelContext, packetWithSendMode.getPacket().logstr());

					if (packetWithSendMode != null)
					{
						synchronized (packetWithSendMode)
						{
							//							log.error("{},释放通知:{}", channelContext, packetWithSendMode.getPacket().logstr());
							packetWithSendMode.notify();
						}
					}
				}

				try
				{
					channelContext.traceClient(ClientAction.AFTER_SEND, packet, null);
					aioListener.onAfterSent(channelContext, packet, isSentSuccess);
				} catch (Exception e)
				{
					log.error(e.toString(), e);
				}
			} else
			{
				List<P> ps = (List<P>) packets;
				if (isSentSuccess)
				{
					packetCount = ps.size();
					groupStat.getSentPacket().addAndGet(packetCount);
				}

				for (P p : ps)
				{
					try
					{
						channelContext.traceClient(ClientAction.AFTER_SEND, p, null);
						aioListener.onAfterSent(channelContext, p, isSentSuccess);
					} catch (Exception e)
					{
						log.error(e.toString(), e);
					}
				}
			}

			if (!isSentSuccess)
			{
				Aio.close(channelContext, throwable, "写数据返回:" + result);
			}
		} catch (Exception e)
		{
			log.error(e.toString(), e);
		} finally
		{

		}
	}

	/**
	 * @return the writeSemaphore
	 */
	public java.util.concurrent.Semaphore getWriteSemaphore()
	{
		return writeSemaphore;
	}

}
