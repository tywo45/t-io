package org.tio.webpack.compress;

/**
 * @author tanyaowu 
 * 2017年11月20日 上午11:03:45
 */
public interface ResCompressor {

	/**
	 * 
	 * @param filePath 
	 * @param srcContent 原内容
	 * @return 压缩后的内容
	 * @author tanyaowu
	 */
	public String compress(String filePath, String srcContent);
	
	String CHARSET = "utf-8";
	
	String DOC = "compressed by http://www.t-io.org";
}
