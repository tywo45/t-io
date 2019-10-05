package org.tio.utils.hutool;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2019年10月5日 下午1:08:58
 */
public class CollUtil {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(CollUtil.class);

	/**
	 * 
	 * @author tanyaowu
	 */
	private CollUtil() {
	}

	/**
	 * 集合是否为空
	 * 
	 * @param collection 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 集合是否为非空
	 * 
	 * @param collection 集合
	 * @return 是否为非空
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		return false == isEmpty(collection);
	}

}
