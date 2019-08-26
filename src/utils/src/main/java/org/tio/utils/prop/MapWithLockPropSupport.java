package org.tio.utils.prop;

import java.util.HashMap;

import org.tio.utils.lock.MapWithLock;

/**
 * @author tanyaowu
 * 2017年8月18日 下午5:36:02
 */
public class MapWithLockPropSupport implements IPropSupport {

	private final MapWithLock<String, Object> props = new MapWithLock<>(new HashMap<String, Object>(8));

	/**
	 *
	 * @author tanyaowu
	 */
	public MapWithLockPropSupport() {
	}

	@Override
	public void clearAttribute() {
		props.clear();
	}
	
	/**
	 * 同：clearAttribute()
	 */
	public void clear() {
		clearAttribute();
	}

	/**
	 *
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Object getAttribute(String key) {
		return get(key);
	}
	
	/**
	 * 同：getAttribute(String key)
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return props.getObj().get(key);
	}

	//	private void initProps() {
	//		if (props == null) {
	//			synchronized (this) {
	//				if (props == null) {
	//					props = new MapWithLock<>(new HashMap<String, Object>(10));
	//				}
	//			}
	//		}
	//	}

	/**
	 * @param key
	 * @author tanyaowu
	 */
	@Override
	public void removeAttribute(String key) {
		remove(key);
	}
	
	/**
	 * 同：removeAttribute(String key)
	 * @param key
	 */
	public void remove(String key) {
		props.remove(key);
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	@Override
	public void setAttribute(String key, Object value) {
		set(key, value);
	}
	
	/**
	 * 同：setAttribute(String key, Object value)
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		props.put(key, value);
	}
}
