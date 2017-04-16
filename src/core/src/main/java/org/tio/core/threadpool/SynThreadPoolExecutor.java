package org.tio.core.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.tio.core.threadpool.intf.SynRunnableIntf;

/**
 * 
 * @author tanyaowu 
 * 2017年4月4日 上午9:25:21
 */
public class SynThreadPoolExecutor<T extends SynRunnableIntf> extends ThreadPoolExecutor implements java.lang.Comparable<SynThreadPoolExecutor<T>>
{
//	private static Logger log = LoggerFactory.getLogger(SynThreadPoolExecutor.class);

	/** The Constant CORE_POOL_NUM. */
	public final static int CORE_POOL_NUM = 5;

	/** The Constant MAX_POOL_NUM. */
	public final static int MAX_POOL_NUM = 40;

	/** The Constant KEEP_ALIVE_TIME. */
	public final static int KEEP_ALIVE_TIME = 90;

	/** The Constant TIME_UNIT. */
	public final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	/** The Constant RUNNABLE_QUEUE. */
	public final static SynchronousQueue<Runnable> RUNNABLE_QUEUE = new SynchronousQueue<Runnable>(); // 存放runnable的队列

	/** The name. */
	private String name = null;

	/**
	 * 
	 * @param name
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor(String name)
	{
		this(CORE_POOL_NUM, MAX_POOL_NUM, KEEP_ALIVE_TIME, RUNNABLE_QUEUE, DefaultThreadFactory.getInstance(name, null), name);
	}

	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param name
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, String name)
	{
		this(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, RUNNABLE_QUEUE, DefaultThreadFactory.getInstance(name, null), name);
	}

	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param runnableQueue
	 * @param name
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, String name)
	{
		this(corePoolSize, maximumPoolSize, keepAliveTime, runnableQueue, DefaultThreadFactory.getInstance(name, null), name);
	}

	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param runnableQueue
	 * @param handler
	 * @param name
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, RejectedExecutionHandler handler, String name)
	{
		this(corePoolSize, maximumPoolSize, keepAliveTime, runnableQueue, DefaultThreadFactory.getInstance(name, null), handler, name);
	}

	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param runnableQueue
	 * @param threadFactory
	 * @param handler
	 * @param name
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler, String name)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, TIME_UNIT, runnableQueue, threadFactory, handler);
		this.name = name;
	}

	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param runnableQueue
	 * @param threadFactory
	 * @param name
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, TIME_UNIT, runnableQueue, threadFactory);
		this.name = name;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		RejectedExecutionHandler handler = new DefaultRejectedExecutionHandler(this);
		this.setRejectedExecutionHandler(handler);
	}

	/**
	 * 
	 * @param runnable
	 * @return
	 * @author: tanyaowu
	 */
	private boolean checkBeforeExecute(T runnable)
	{
		ReadWriteLock runningLock = runnable.runningLock();
		Lock writeLock = runningLock.writeLock();
		boolean tryLock = false;
		try
		{
			tryLock = writeLock.tryLock();
			return tryLock;
		} finally
		{
			if (tryLock)
			{
				writeLock.unlock();
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Runnable _runnable)
	{
		T runnable = (T) _runnable;
		if (checkBeforeExecute(runnable))
		{
//			log.error("提交任务:{}", _runnable.getClass().getSimpleName());
			super.execute(runnable);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> Future<R> submit(Runnable _runnable, R result)
	{
		T runnable = (T) _runnable;
		if (checkBeforeExecute(runnable))
		{
			Future<R> ret = super.submit(runnable, result);
			return ret;
		} else
		{
			return null;
		}
	}
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 
	 * @param o
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public int compareTo(SynThreadPoolExecutor<T> o)
	{
		if (o.getCompletedTaskCount() > this.getCompletedTaskCount())
		{
			return -1;
		} else if (o.getCompletedTaskCount() < this.getCompletedTaskCount())
		{
			return 1;
		}

		return this.getName().compareTo(o.getName());

	}
}
