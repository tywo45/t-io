package org.tio.webpack.cache;

import java.io.Serializable;

/**
 * @author tanyaowu 
 * 2017年11月20日 上午11:00:24
 */
public class CacheVo implements Serializable {
	private static final long serialVersionUID = -1693751347296834323L;

	/**
	 * 
	 * @author tanyaowu
	 */
	public CacheVo() {
	}

	private String path;

	private byte[] initBytes;

	private byte[] compressedBytes;

//	private String initStr;

//	private String compressedStr;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public byte[] getInitBytes() {
		return initBytes;
	}

	public void setInitBytes(byte[] initBytes) {
		this.initBytes = initBytes;
	}

	public byte[] getCompressedBytes() {
		return compressedBytes;
	}

	public void setCompressedBytes(byte[] compressedBytes) {
		this.compressedBytes = compressedBytes;
	}

//	/**
//	 * @return the initStr
//	 */
//	public String getInitStr() {
//		return initStr;
//	}
//
//	/**
//	 * @param initStr the initStr to set
//	 */
//	public void setInitStr(String initStr) {
//		this.initStr = initStr;
//	}

//	/**
//	 * @return the compressedStr
//	 */
//	public String getCompressedStr() {
//		return compressedStr;
//	}
//
//	/**
//	 * @param compressedStr the compressedStr to set
//	 */
//	public void setCompressedStr(String compressedStr) {
//		this.compressedStr = compressedStr;
//	}

}
