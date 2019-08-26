package org.tio.utils.hutool;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Zip工具类
 * 
 * @author looly
 *
 */
public class ZipUtil {
	/**
	 * Gzip压缩处理
	 * 
	 * @param val 被压缩的字节流
	 * @return 压缩后的字节流
	 * @throws UtilException IO异常
	 */
	public static byte[] gzip(byte[] val) throws RuntimeException {
		FastByteArrayOutputStream bos = new FastByteArrayOutputStream(val.length);
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(bos);
			gos.write(val, 0, val.length);
			gos.finish();
			gos.flush();
			val = bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (null != gos) {
				try {
					gos.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
		return val;
	}
}
