package org.tio.websocket.common.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Util {
	
	private final static String ALGORITHM = "SHA-1";
	
	public static byte[] SHA1(byte[] decript) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance(ALGORITHM);
			digest.update(decript);
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] SHA1(String decript) {
		return SHA1(decript.getBytes());
	}

	public static String SHA1(String decript, Charset encoding) {
		byte[] array = SHA1(decript);
		return new String(array, encoding);
	}
}
