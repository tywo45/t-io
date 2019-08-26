package org.tio.utils.hutool;

/**
 * @author tanyaowu 
 * 2018年8月5日 下午7:42:44
 */
public interface ClassScanHandler {

	/**
	 * 
	 * @param clazz 扫描到的class
	 * @author tanyaowu
	 */
	void handler(Class<?> clazz);

}
