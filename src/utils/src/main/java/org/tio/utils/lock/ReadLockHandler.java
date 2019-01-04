package org.tio.utils.lock;

/**
 * @author tanyw
 *
 */
public interface ReadLockHandler <T> {
	
	/**
	 * 
	 * @param t
	 */
	public void handler(T t);
	
}
