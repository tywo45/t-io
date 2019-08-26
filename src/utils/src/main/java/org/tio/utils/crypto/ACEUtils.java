/**
 * 
 */
package org.tio.utils.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 *
 */
public class ACEUtils {

	/**
	 * 
	 */
	private ACEUtils() {

	}

	private static Logger log = LoggerFactory.getLogger(ACEUtils.class);

	/**
	 * 加密
	 * @param sSrc
	 * @param sKey
	 * @param ivStr 使用CBC模式，需要一个向量iv，可增加加密算法的强度
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public static String encrypt(String sSrc, String sKey, String ivStr) throws Exception {
		// 判断Key是否正确
		if (sKey == null) {
			throw new Exception("Key为空");
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
			throw new Exception("Key长度不是16位");
		}
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
		IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());

		return new sun.misc.BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
	}

	/**
	 * 解密
	 * @param sSrc
	 * @param sKey
	 * @param ivStr
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public static String decrypt(String sSrc, String sKey, String ivStr) throws Exception {
		// 判断Key是否正确
		if (sKey == null) {
			throw new Exception("Key为空");
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
			throw new Exception("Key长度不是16位");
		}
		byte[] raw = sKey.getBytes("ASCII");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		byte[] encrypted1 = new sun.misc.BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
		byte[] original = cipher.doFinal(encrypted1);
		String originalString = new String(original);
		return originalString;
	}

	public static void main(String[] args) throws Exception {
		/*
		 * 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
		 * 此处使用AES-128-CBC加密模式，key需要为16位。
		 */
		String cKey = "xOezYlYsPebzEolO";
		String ivStr = cKey;
		// 需要加密的字串
		String cSrc = "8780882";
		log.info(cSrc);
		// 加密
		long lStart = System.currentTimeMillis();
		String enString = encrypt(cSrc, cKey, ivStr);
		log.info("加密后的字串是：" + enString);

		long lUseTime = System.currentTimeMillis() - lStart;
		log.info("加密耗时：" + lUseTime + "毫秒");
		// 解密
		lStart = System.currentTimeMillis();
		String DeString = decrypt(enString, cKey, ivStr);
		log.info("解密后的字串是：" + DeString);
		lUseTime = System.currentTimeMillis() - lStart;
		log.info("解密耗时：" + lUseTime + "毫秒");
	}
	//
	//	/**
	//	 * @param args
	//	 */
	//	public static void main(String[] args) {
	//		PropInit.init();
	//
	//		String content = "test中文";
	//
	//		//随机生成密钥
	//		byte[] key = "uPezilSoTLyzkMop".getBytes();//SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
	//		log.info(new String(key));
	//
	//		//构建
	//		AES aes = SecureUtil.aes(key);
	//
	//		//加密
	//		byte[] encrypt = aes.encrypt(content);
	//		//解密
	//		byte[] decrypt = aes.decrypt(encrypt);
	//
	//		//加密为16进制表示
	//		String encryptHex = Base64.decodeToString(encrypt);//.encryptHex(content);
	//		log.info(encryptHex);
	//		//解密为原字符串
	//		String decryptStr = aes.decryptStr(encryptHex);
	//
	//		log.info(encryptHex);
	//		log.info(decryptStr);
	//	}

}
