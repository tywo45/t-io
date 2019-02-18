package org.tio.webpack.compress;

import org.tio.webpack.compress.css.TioCssCompressor;
import org.tio.webpack.compress.html.TioHtmlCompressor;
import org.tio.webpack.compress.js.TioJsCompressor;
import org.tio.webpack.model.Root;

/**
 * @author tanyaowu 
 * 2017年11月20日 上午11:07:59
 */
public class ResCompressorFactory {

	/**
	 * 
	 * @author tanyaowu
	 */
	public ResCompressorFactory() {
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 * 
	 * @param extension
	 * @return
	 * @author tanyaowu
	 */
	public static ResCompressor get(String extension) {
		if ("js".equalsIgnoreCase(extension)) {
			//			return YuiJsCompressor.ME;
			return TioJsCompressor.ME;
		} else if ("css".equalsIgnoreCase(extension)) {
			return TioCssCompressor.ME;
		} else if ("html".equalsIgnoreCase(extension) || "htm".equalsIgnoreCase(extension)) {
			return TioHtmlCompressor.ME;
		}

		return null;
	}

	public static boolean isNeedCompress(Root model, String extension) {
		if ("js".equalsIgnoreCase(extension)) {
			return model.getCompress().getJs();
		} else if ("css".equalsIgnoreCase(extension)) {
			return model.getCompress().getCss();
		} else if ("html".equalsIgnoreCase(extension) || "htm".equalsIgnoreCase(extension)) {
			return model.getCompress().getHtml();
		}

		return false;
	}
}
