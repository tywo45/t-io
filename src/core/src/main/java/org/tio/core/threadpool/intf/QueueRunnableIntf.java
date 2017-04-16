package org.tio.core.threadpool.intf;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 队列数据处理任务接口.
 *
 * @author 谭耀武
 * @param <T> 队列中存的数据类型
 * 2012-1-4
 */
public interface QueueRunnableIntf<T>
{

	/**
	 * 获取数据队列.
	 *
	 * @return 保存着要处理的数据的队列
	 */
	ConcurrentLinkedQueue<T> getMsgQueue();
}
