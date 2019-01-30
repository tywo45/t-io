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
	
	String DOC = "\r\n1、全新编译压缩技术tio-webpack(基于t-io)为本站提供压缩渲染"
			+ "\r\n2、如果本站把您的IP拉黑，冤有头债有主，请去这里找tio ： https://github.com/tywo45/t-io"
			+ "\r\n";
}
