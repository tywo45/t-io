package org.tio.utils.thread.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 上午9:23:12
 */
public abstract class AbstractQueueRunnable<T> extends AbstractSynRunnable {
	private static final Logger log = LoggerFactory.getLogger(AbstractQueueRunnable.class);

	/** The msg queue. */
	protected ConcurrentLinkedQueue<T> msgQueue = new ConcurrentLinkedQueue<>();

	/**
	 *
	 * @param executor
	 * @author tanyaowu
	 */
	public AbstractQueueRunnable(Executor executor) {
		super(executor);
	}

	/**
	 * @return
	 *
	 */
	public boolean addMsg(T t) {
		if (this.isCanceled()) {
			log.error("任务已经取消");
			return false;
		}

		return msgQueue.add(t);
	}

	/**
	 * 清空处理的队列消息
	 */
	public void clearMsgQueue() {
		msgQueue.clear();
	}

	@Override
	public boolean isNeededExecute() {
		return  !this.isCanceled() && msgQueue.size() > 0;
	}

	/**
	 * 获取消息队列
	 * @return
	 */
	public ConcurrentLinkedQueue<T> getMsgQueue() {
		return msgQueue;
	}
}
