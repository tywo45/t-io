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
	
	private String initData;

	private String compressedData;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getInitData() {
		return initData;
	}

	public void setInitData(String initData) {
		this.initData = initData;
	}

	public String getCompressedData() {
		return compressedData;
	}

	public void setCompressedData(String compressedData) {
		this.compressedData = compressedData;
	}


	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}
}
