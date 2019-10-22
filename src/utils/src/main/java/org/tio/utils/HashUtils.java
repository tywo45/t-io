package org.tio.utils;

/**
 * hash工具类，仅供tio内部使用，外部请勿使用
 * @author tanyaowu
 */
public class HashUtils {
	private static final long	OFFSET_BASIS	= 2166136261L;
	private static final long	PRIME			= 16777619;

	/**
	 * @param src
	 * @return
	 */
	public static int hash(byte[] src) {
		return hash(src, 0, src.length);
	}

	/**
	 * @param src
	 * @param start
	 * @param len
	 * @return
	 */
	public static int hash(byte[] src, int start, int len) {
		long hash = OFFSET_BASIS;
		int end = start + len;
		for (int i = start; i < end; i++) {
			byte b = src[i];
			hash ^= b;
			hash *= PRIME;
		}

		return (int) hash;
	}

}
