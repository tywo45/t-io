package org.tio.utils.lock;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.lock.ReadWriteLockHandler.ReadWriteRet;

/**
 * 锁对象工具类
 */
public class LockUtils {
	private static Logger				log						= LoggerFactory.getLogger(LockUtils.class);
	private static final String			LOCK_TYPE_OBJ			= "OBJ";
	private static final String			LOCK_TYPE_RW			= "RW";
	private static final Object			defaultLockObjForObj	= new Object();
	private static final Object			defaultLockObjForRw		= new Object();
	private static final CaffeineCache	LOCAL_LOCKS				= CaffeineCache.register(LockUtils.class.getName() + LOCK_TYPE_OBJ, null, 3600L);
	private static final CaffeineCache	LOCAL_READWRITE_LOCKS	= CaffeineCache.register(LockUtils.class.getName() + LOCK_TYPE_RW, null, 3600L);

	/**
	 * 获取锁对象，用于synchronized(lockObj)
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	public static Serializable getLockObj(String key) {
		return getLockObj(key, null);
	}

	/**
	 * 获取锁对象，用于synchronized(lockObj)
	 * @param key
	 * @param myLock 获取LockObj的锁，可以为null
	 * @return
	 * @author tanyaowu
	 */
	public static Serializable getLockObj(String key, Object myLock) {
		Serializable lock = LOCAL_LOCKS.get(key);
		if (lock == null) {
			Object ml = myLock;
			if (ml == null) {
				ml = defaultLockObjForObj;
			}
			synchronized (ml) {
				lock = LOCAL_LOCKS.get(key);
				if (lock == null) {
					lock = new Serializable() {
						private static final long serialVersionUID = 255956860617836425L;
					};
					LOCAL_LOCKS.put(key, lock);
				}
			}
		}
		return lock;
	}

	/**
	 * 获取读写锁
	 * @param key
	 * @param myLock 获取ReentrantReadWriteLock的锁，可以为null
	 * @return
	 * @author tanyaowu
	 */
	public static ReentrantReadWriteLock getReentrantReadWriteLock(String key, Object myLock) {
		ReentrantReadWriteLock lock = (ReentrantReadWriteLock) LOCAL_READWRITE_LOCKS.get(key);
		if (lock == null) {
			Object ml = myLock;
			if (ml == null) {
				ml = defaultLockObjForRw;
			}
			synchronized (ml) {
				lock = (ReentrantReadWriteLock) LOCAL_READWRITE_LOCKS.get(key);
				if (lock == null) {
					lock = new ReentrantReadWriteLock();
					LOCAL_READWRITE_LOCKS.put(key, lock);
				}
			}
		}
		return lock;
	}

	/**
	 * 用读写锁操作
	 * @param key
	 * @param myLock 获取ReentrantReadWriteLock的锁，可以为null
	 * @param readWriteLockHandler 小心：该对象的write()方法和read()只会被执行一个
	 * @throws Exception 
	 */
	public static ReadWriteRet runReadOrWrite(String key, Object myLock, ReadWriteLockHandler readWriteLockHandler) throws Exception {
		ReentrantReadWriteLock rwLock = getReentrantReadWriteLock(key, myLock);
		ReadWriteRet ret = new ReadWriteRet();
		WriteLock writeLock = rwLock.writeLock();
		boolean tryWrite = writeLock.tryLock();
		if (tryWrite) {
			try {
				Object writeRet = readWriteLockHandler.write();
				ret.isWriteRunned = true;
				ret.writeRet = writeRet;
			} finally {
				writeLock.unlock();
			}
		} else {
			ReadLock readLock = rwLock.readLock();
			boolean tryRead = false;
			try {
				tryRead = readLock.tryLock(120, TimeUnit.SECONDS);
				if (tryRead) {
					try {
						Object readRet = readWriteLockHandler.read();
						ret.isReadRunned = true;
						ret.readRet = readRet;
					} finally {
						readLock.unlock();
					}
				}
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
			}
		}

		return ret;
	}

}
