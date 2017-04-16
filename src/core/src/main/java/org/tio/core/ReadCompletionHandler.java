package org.tio.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;
import org.tio.core.task.DecodeRunnable;
import org.tio.core.utils.AioUtils;

/**
 * 
 * @author tanyaowu 
 * 2017年4月4日 上午9:22:04
 */
public class ReadCompletionHandler<SessionContext, P extends Packet, R> implements CompletionHandler<Integer, ByteBuffer>
{

	private static Logger log = LoggerFactory.getLogger(ReadCompletionHandler.class);
	private ChannelContext<SessionContext, P, R> channelContext = null;
	private ByteBuffer readByteBuffer;

	//	private ByteBuffer byteBuffer = ByteBuffer.allocate(ChannelContext.READ_BUFFER_SIZE);

	/**
	 * 
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public ReadCompletionHandler(ChannelContext<SessionContext, P, R> channelContext)
	{
		this.setChannelContext(channelContext);
		this.readByteBuffer = ByteBuffer.allocate(channelContext.getGroupContext().getReadBufferSize());
	}

	@Override
	public void completed(Integer result, ByteBuffer byteBuffer)
	{
//		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		if (result > 0)
		{
//			ByteBuffer newByteBuffer = ByteBufferUtils.copy(readByteBuffer, 0, readByteBuffer.position());
			DecodeRunnable<SessionContext, P, R> decodeRunnable = channelContext.getDecodeRunnable();
			readByteBuffer.flip();
			decodeRunnable.setNewByteBuffer(readByteBuffer);
			decodeRunnable.run();
//			decodeRunnable.addMsg(newByteBuffer);
//			groupContext.getDecodeExecutor().execute(decodeRunnable);
		} else if (result == 0)
		{
			log.error("{}读到的数据长度为0", channelContext);
		} else if (result < 0)
		{
			Aio.close(channelContext, null, "读数据时返回" + result);
		}

		if (AioUtils.checkBeforeIO(channelContext))
		{
			AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
			readByteBuffer.position(0);
			readByteBuffer.limit(readByteBuffer.capacity());
			asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, this);
		}

	}

	/**
	 * 
	 * @param exc
	 * @param byteBuffer
	 * @author: tanyaowu
	 */
	@Override
	public void failed(Throwable exc, ByteBuffer byteBuffer)
	{
		Aio.close(channelContext, exc, "读数据时发生异常");

	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public ChannelContext<SessionContext, P, R> getChannelContext()
	{
		return channelContext;
	}

	/**
	 * 
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void setChannelContext(ChannelContext<SessionContext, P, R> channelContext)
	{
		this.channelContext = channelContext;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public ByteBuffer getReadByteBuffer()
	{
		return readByteBuffer;
	}

}
