package org.tio.utils.prop;

/**
 * 属性支持接口
 * @author tanyaowu
 * 2017年8月18日 下午5:34:14
 */
public interface IPropSupport {
	/**
	 * 清除所有属性
	 * 
	 * @author: tanyaowu
	 */
	public void clearAttribute();

	/**
	 * 获取属性
	 * @param key
	 * @return
	 * @author: tanyaowu
	 */
	public Object getAttribute(String key);

	/**
	 * 删除属性
	 * @param key
	 * @author: tanyaowu
	 */
	public void removeAttribute(String key);

	/**
	 * 设置属性
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	public void setAttribute(String key, Object value);
}
