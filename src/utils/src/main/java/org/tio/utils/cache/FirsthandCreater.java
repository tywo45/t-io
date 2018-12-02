/**
 * 
 */
package org.tio.utils.cache;

import java.io.Serializable;

/**
 * 一手对象（即非缓存对象）创建者
 * @author tanyaowu
 *
 */
public interface FirsthandCreater<T extends Serializable> {

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public T create();

}
