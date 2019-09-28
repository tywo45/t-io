package org.tio.utils.queue;

/**
 * 满员等待队列
 * @author tanyaowu 
 * 2019年9月28日 上午9:36:45
 */
public interface FullWaitQueue<T> {
	/**
	 * 向队列尾添加一个元素，如果队列已经满了，则等待一段时间
	 * @param t
	 * @return
	 * @author tanyaowu
	 */
	public boolean add(T t);

	/**
	* Retrieves and removes the head of this queue,
	* or returns {@code null} if this queue is empty.
	*
	* @return the head of this queue, or {@code null} if this queue is empty
	*/
	public T poll();
	
	public void clear();
	
	public int size();
}
