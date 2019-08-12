package org.tio.utils.cache;

import java.io.Serializable;

/**
 * @author tanyaowu
 * 2017年8月10日 上午11:38:26
 */
public interface ICache {
	/**
	 * 有时候需要放一个空对象到缓存中
	 * @author tanyaowu
	 */
	public static class NullClass implements Serializable {
		private static final long serialVersionUID = -2298613658358477523L;
	}
	
	/**
	 * 用于临时存放于缓存中的对象，防止缓存null攻击
	 */
	public static final NullClass NULL_OBJ = new NullClass();

	/**
	 * 在本地最大的过期时间，这样可以防止内存爆掉，单位：秒
	 */
	public static int MAX_EXPIRE_IN_LOCAL = 10 * 60;

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public String getCacheName();

	/**
	 *
	 * 清空所有缓存
	 * @author tanyaowu
	 */
	void clear();

	/**
	 * 根据key获取value
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	public Serializable get(String key);

	/**
	 * 根据key获取value
	 * @param key
	 * @param clazz
	 * @return
	 * @author: tanyaowu
	 */
	public <T> T get(String key, Class<T> clazz);

	/**
	 * 获取所有的key
	 * @return
	 * @author tanyaowu
	 */
	Iterable<String> keys();

	/**
	 * 将key value保存到缓存中
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	public void put(String key, Serializable value);

	//	/**
	//	 * 将key value更新到缓存中
	//	 * @param key
	//	 * @param value
	//	 * @author tanyaowu
	//	 */
	//	public void update(String key, Serializable value);

	/**
	 * 删除一个key
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	public void remove(String key);

	/**
	 * 临时添加一个值，用于防止缓存穿透攻击
	 * @param key
	 * @param value
	 */
	public void putTemporary(String key, Serializable value);

	/**
	 * 对象还会存活多久。
	 * @return currTime in milliseconds
	 *          -2 if the key does not exist.
	 *          -1 if the key exists but has no associated expire.
	 */
	public long ttl(String key);

	/**
	 * 
	 * @return
	 */
	public Long getTimeToLiveSeconds();

	/**
	 * 
	 * @return
	 */
	public Long getTimeToIdleSeconds();
}
