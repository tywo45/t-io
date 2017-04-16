package org.tio.core.syn;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.tio.core.ObjWithLock;

public class ReadWriteMap<K, V> implements Map<K, V>
{

	private ObjWithLock<Map<K, V>> objWithLock = null;
	private Map<K, V> map = null;
	private ReentrantReadWriteLock readWriteLock = null;
	private WriteLock writeLock = null;
	private ReadLock readLock = null;

	/**
	 * 
	 * @author: tanyaowu
	 * 
	 */
	public ReadWriteMap(Map<K, V> map)
	{
		if (map == null)
		{
			throw new RuntimeException("map 不允许为空");
		}
		this.map = map;
		this.objWithLock = new ObjWithLock<>(this.map);
		this.readWriteLock = this.objWithLock.getLock();
		this.writeLock = this.readWriteLock.writeLock();
		this.readLock = this.readWriteLock.readLock();
	}
	/** 
	 * @see java.util.Map#size()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public int size()
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.size();
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#isEmpty()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public boolean isEmpty()
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.isEmpty();
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#containsKey(java.lang.Object)
	 * 
	 * @param key
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public boolean containsKey(Object key)
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.containsKey(key);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#containsValue(java.lang.Object)
	 * 
	 * @param value
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public boolean containsValue(Object value)
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.containsValue(value);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#get(java.lang.Object)
	 * 
	 * @param key
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public V get(Object key)
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.get(key);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public V put(K key, V value)
	{
		Lock lock = writeLock;
		try
		{
			lock.lock();
			return map.put(key, value);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#remove(java.lang.Object)
	 * 
	 * @param key
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public V remove(Object key)
	{
		Lock lock = writeLock;
		try
		{
			lock.lock();
			return map.remove(key);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#putAll(java.util.Map)
	 * 
	 * @param m
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{

		Lock lock = writeLock;
		try
		{
			lock.lock();
			map.putAll(m);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}

	}

	/** 
	 * @see java.util.Map#clear()
	 * 
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public void clear()
	{

		Lock lock = writeLock;
		try
		{
			lock.lock();
			map.clear();
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}

	}

	/** 
	 * @see java.util.Map#keySet()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public Set<K> keySet()
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.keySet();
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#values()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public Collection<V> values()
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.values();
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	/** 
	 * @see java.util.Map#entrySet()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月8日 上午9:46:16
	 * 
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		Lock lock = readLock;
		try
		{
			lock.lock();
			return map.entrySet();
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

}
