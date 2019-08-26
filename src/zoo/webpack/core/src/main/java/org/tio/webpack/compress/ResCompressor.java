package org.tio.webpack.compress;

import org.tio.utils.SysConst;

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

	String DOC = "\r\n1、t-io提供压缩能力" + "\r\n2、不仅仅是百万级网络编程框架 ： https://www.t-io.org" + SysConst.CRLF;
}
