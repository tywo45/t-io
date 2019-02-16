package org.tio.webpack.compress;

/**
 * @author tanyaowu 
 * 2017年11月20日 上午11:03:45
 */
public interface ResCompressor {

	/**
	 * 
	 * @param filePath 
	 * @param initStr 原内容
	 * @return 压缩后的内容
	 * @author tanyaowu
	 */
	public String compress(String filePath, String initStr);

	String CHARSET = "utf-8";

	String DOC = "\r\n1、全新编译压缩技术tio-webpack为本站提供压缩渲染" + "\r\n2、降低自建IM门槛，让工程师成为互联网架构师更容易 ： https://www.t-io.org" + "\r\n";
}
