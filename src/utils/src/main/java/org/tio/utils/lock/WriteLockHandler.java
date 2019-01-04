package org.tio.utils.lock;

/**
 * @author tanyw
 *
 */
public interface WriteLockHandler <T> {
	
	/**
	 * 
	 * @param t
	 */
	public void handler(T t);
	
}
