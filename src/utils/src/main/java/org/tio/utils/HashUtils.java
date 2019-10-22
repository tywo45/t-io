package org.tio.utils;

/**
 * hash工具类，仅供tio内部使用，外部请勿使用
 * @author tanyaowu
 */
public class HashUtils {
	private static final int	OFFSET_BASIS	= (int) 2166136261L;
	private static final int	PRIME			= 16777619;

	/**
	 * @param src
	 * @return
	 */
	public static int hashFNV1(byte[] src) {
		return hashFNV1(src, 0, src.length);
	}

	/**
	 * FNV1算法
	 * @param src
	 * @param start
	 * @param len
	 * @return
	 */
	public static int hashFNV1(byte[] src, int start, int len) {
		int hash = OFFSET_BASIS;
		int end = start + len;
		for (int i = start; i < end; i++) {
			hash = (hash ^ src[i]) * PRIME;
		}
		return hash;
	}

	/**
	 * 每位乘以31相加
	 * @param src
	 * @param start
	 * @param len
	 * @return
	 * @author tanyaowu
	 */
	public static int hash31(byte[] src, int start, int len) {
		int hash = 1;
		int end = start + len;
		for (int i = start; i < end; i++) {
			hash = 31 * hash + src[i];
		}
		return hash;
	}

}
