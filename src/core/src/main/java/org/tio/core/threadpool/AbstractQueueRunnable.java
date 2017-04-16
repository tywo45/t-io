package org.tio.core.threadpool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.threadpool.intf.QueueRunnableIntf;

/**
 * 
 * @author tanyaowu 
 * 2017年4月4日 上午9:23:12
 */
public abstract class AbstractQueueRunnable<T> extends AbstractSynRunnable implements QueueRunnableIntf<T>
{
	private static final Logger log = LoggerFactory.getLogger(AbstractQueueRunnable.class);
	
	/**
	 * 
	 * @param executor
	 * @author: tanyaowu
	 */
	public AbstractQueueRunnable(Executor executor)
	{
		super(executor);
	}

	@Override
	public boolean isNeededExecute()
	{
		return msgQueue.size() > 0;
	}

	/** The msg queue. */
	protected ConcurrentLinkedQueue<T> msgQueue = new ConcurrentLinkedQueue<T>();

	/**
	 * 
	 */
	public void addMsg(T t)
	{
		if (this.isCanceled())
		{
			log.error("任务已经取消");
			return;
		}
		
		getMsgQueue().add(t);
	}

	/**
	 * 
	 */
	@Override
	public ConcurrentLinkedQueue<T> getMsgQueue()
	{
		return msgQueue;
	}

	/**
	 * Sets the msg queue.
	 *
	 * @param msgQueue the new msg queue
	 */
	public void setMsgQueue(ConcurrentLinkedQueue<T> msgQueue)
	{
		this.msgQueue = msgQueue;
	}

}
