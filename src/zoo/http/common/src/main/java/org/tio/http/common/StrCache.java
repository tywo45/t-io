package org.tio.http.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.HashUtils;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.LockUtils;

/**
 * 本类主要用于可枚举的String，不要滥用
 * @author tanyaowu
 */
public class StrCache {
	private static Logger						log					= LoggerFactory.getLogger(StrCache.class);
	/**
	 * key: byte[]对应的hashcode
	 * value: byte[]对应的字符串
	 */
	private static final Map<Integer, String>	BYTES_STRING_MAP	= new HashMap<>(100);
	/**
	 * key : 原字符串
	 * value: 小写后的字符串
	 */
	private static final Map<Integer, String>	INIT_LOWERCASE_MAP	= new HashMap<>(100);

	/**
	 * 
	 */
	public StrCache() {

	}

	//	public static String get(byte[] allbs, int start, int len) {
	//		byte[] bs = new byte[len];
	//		System.arraycopy(allbs, start, bs, 0, len);
	//
	//		return get(bs);
	//	}

	/**
	 * 
	 * @param allbs
	 * @param start
	 * @param len
	 * @return
	 */
	public static String get(byte[] allbs, int start, int len) {
		int hashcode = HashUtils.hash(allbs, start, len);
		String str = BYTES_STRING_MAP.get(hashcode);
		if (str == null) {
			if (BYTES_STRING_MAP.size() > 1000) {
				return new String(allbs, start, len);
			}

			try {
				LockUtils.runWriteOrWaitRead("StrCache:getBytes" + hashcode, BYTES_STRING_MAP, () -> {
					String str2 = BYTES_STRING_MAP.get(hashcode);
					if (str2 == null) {
						str2 = new String(allbs, start, len);
						BYTES_STRING_MAP.put(hashcode, str2);
					}
				});
			} catch (Exception e) {
				log.error(e.toString(), e);
			}

			str = BYTES_STRING_MAP.get(hashcode);
		} else {
//			System.out.println("1:" + str);
		}
		return str;
	}

	/**
	 * 
	 * @param initStr
	 * @return
	 */
	public static String getLowercase(String initStr) {
		if (StrUtil.isBlank(initStr)) {
			return initStr;
		}
		int hashcode = initStr.hashCode();
		String str = INIT_LOWERCASE_MAP.get(hashcode);
		if (str == null) {
			if (INIT_LOWERCASE_MAP.size() > 1000) {
				return initStr.toLowerCase();
			}

			try {
				LockUtils.runWriteOrWaitRead("StrCache:getLowercase" + hashcode, INIT_LOWERCASE_MAP, () -> {
					String str2 = INIT_LOWERCASE_MAP.get(hashcode);
					if (str2 == null) {
						str2 = initStr.toLowerCase();
						INIT_LOWERCASE_MAP.put(hashcode, str2);
					}
				});
			} catch (Exception e) {
				log.error(e.toString(), e);
			}

			str = INIT_LOWERCASE_MAP.get(hashcode);
		} else {
//			System.out.println("2:" + str);
		}
		return str;

	}

}
