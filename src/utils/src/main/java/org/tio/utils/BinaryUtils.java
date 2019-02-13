package org.tio.utils;

import java.util.Random;

/**
 * 邀请码工具类
 * 参考：https://my.oschina.net/u/1469495/blog/470599
 */
public class BinaryUtils {

	/** 自定义进制(0,1没有加入,容易与o,l混淆) */
	private static final char[] r = "abcdefghijk23456789mnpqrstuvwxy".toCharArray(); //不要有Z，因为下面有一个补全的字符是Z

	/** (不能与自定义进制有重复) */
	private static final char b = 'z';

	/** 进制长度 */
	private static final int binLen = r.length;

	/** 序列最小长度 */
	private static final int s = 6;

	/**
	 * 根据ID生成六位随机码
	 * @param id ID
	 * @return 随机码
	 */
	public static String encode(int id) {
		char[] buf = new char[32];
		int charPos = 32;

		while ((id / binLen) > 0) {
			int ind = (int) (id % binLen);
			// System.out.println(num + "-->" + ind);
			buf[--charPos] = r[ind];
			id /= binLen;
		}
		buf[--charPos] = r[(int) (id % binLen)];
		// System.out.println(num + "-->" + num % binLen);
		String str = new String(buf, charPos, (32 - charPos));
		// 不够长度的自动随机补全
		if (str.length() < s) {
			StringBuilder sb = new StringBuilder();
			sb.append(b);
			Random rnd = new Random();
			for (int i = 1; i < s - str.length(); i++) {
				sb.append(r[rnd.nextInt(binLen)]);
			}
			str += sb.toString();
		}
		return str;
	}

	public static long decode(String code) {
		char chs[] = code.toCharArray();
		long res = 0L;
		for (int i = 0; i < chs.length; i++) {
			int ind = 0;
			for (int j = 0; j < binLen; j++) {
				if (chs[i] == r[j]) {
					ind = j;
					break;
				}
			}
			if (chs[i] == b) {
				break;
			}
			if (i > 0) {
				res = res * binLen + ind;
			} else {
				res = ind;
			}
		}
		return res;
	}

	public static void main(String[] args) {
		//		Random random = new Random();
		int start = 1000000000;
		int end = start + 1000;
		for (int i = start; i < end; ++i) {
			int id = i;//RandomUtil.randomInt(1, 200000);
			String code = encode(id);
			int nid = (int) decode(code);
			if (id != nid) {
				System.out.println(id + " -> " + code + " -> " + nid);
			}
			System.out.println(id + " -> " + code + " -> " + nid);
		}
	}

	//	public static String[] chars = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	//
	//	public static String getInviteCode() {
	//		StringBuffer shortBuffer = new StringBuffer();
	//		String uuid = UUID.randomUUID().toString().replace("-", "");
	//		for (int i = 0; i < 6; i++) {
	//			String str = uuid.substring(i * 5, i * 5 + 7);
	//			int x = Integer.parseInt(str, 16);
	//			shortBuffer.append(chars[x % 0xA]);
	//		}
	//		return shortBuffer.toString();
	//	}

}
