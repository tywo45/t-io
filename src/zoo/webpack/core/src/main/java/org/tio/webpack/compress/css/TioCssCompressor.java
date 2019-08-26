package org.tio.webpack.compress.css;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.webpack.compress.ResCompressor;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * @author tanyaowu 
 * 2017年11月17日 上午11:26:21
 */
public class TioCssCompressor implements ResCompressor {

	private static Logger log = LoggerFactory.getLogger(TioCssCompressor.class);

	public static final TioCssCompressor me = new TioCssCompressor();

	/**
	 * 
	 * @author tanyaowu
	 */
	private TioCssCompressor() {
	}

	static final String commits = "/*" + DOC + "*/\r\n";

	@Override
	public String compress(String filePath, String srcCssContent) {
		try {
			CssCompressor cssCompressor = new CssCompressor(new StringReader(srcCssContent));

			StringWriter sw = new StringWriter();
			int linebreakpos = -1;
			cssCompressor.compress(sw, linebreakpos);

			String ret = sw.toString();

			if (ret == null || ret.length() == 0) {
				log.warn("压缩后的文件大小为0, {}", filePath);
				return srcCssContent;
			}

			//			byte[] initBytes = srcCssContent.getBytes();
			//			byte[] afterBytes = ret.getBytes();
			//
			//			if (afterBytes.length >= initBytes.length) {
			//				log.warn("CSS压缩后的文件反而较大,  init size:{}, after size:{}, file:{}", initBytes.length, afterBytes.length, filePath);
			//				return srcCssContent;
			//			}

//			return commits + ret;
			return ret;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return srcCssContent;
		}

	}

	/**
	 * @param args
	 * @author tanyaowu
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		byte[] bytes = Files.readAllBytes(new File("E:\\svn\\nbyb\\html\\nbyb_bootstrap\\src\\public\\css\\common.css").toPath());
		String xx = new String(bytes, "utf-8");
		String compiled_code = TioCssCompressor.me.compress("cc.css", xx);
		System.out.println(compiled_code);
		long end = System.currentTimeMillis();
		long iv = end - start;
		System.out.println("耗时:" + iv + "ms");
	}
}
