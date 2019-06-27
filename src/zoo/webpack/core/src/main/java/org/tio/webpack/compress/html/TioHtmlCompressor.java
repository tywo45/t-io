package org.tio.webpack.compress.html;

import java.io.File;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.webpack.compress.ResCompressor;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

/**
 * 
 * @author tanyaowu 
 * 2017年11月17日 下午5:00:38 
 */
public class TioHtmlCompressor implements ResCompressor {
	private static Logger log = LoggerFactory.getLogger(TioHtmlCompressor.class);

	private static HtmlOptions	options	= new HtmlOptions();
	static final String			commits	= "<!-- " + DOC + " -->\r\n";

	public static TioHtmlCompressor me = new TioHtmlCompressor();

	/**
	 * @param compressor
	 * @author tanyaowu
	 */
	public TioHtmlCompressor() {
		super();

		HtmlCompressor compressor = new HtmlCompressor();
		compressor.setEnabled(true);
		compressor.setCompressCss(false);
		compressor.setCompressJavaScript(false);

		compressor.setGenerateStatistics(false);

		compressor.setRemoveComments(options.isRemoveComments());
		compressor.setRemoveMultiSpaces(options.isRemoveMutliSpaces());
		compressor.setRemoveIntertagSpaces(options.isRemoveIntertagSpaces());
		compressor.setRemoveQuotes(options.isRemoveQuotes());
		compressor.setSimpleDoctype(options.isSimpleDoctype());
		compressor.setRemoveScriptAttributes(options.isRemoveScriptAttributes());
		compressor.setRemoveStyleAttributes(options.isRemoveStyleAttributes());
		compressor.setRemoveLinkAttributes(options.isRemoveLinkAttributes());
		compressor.setRemoveFormAttributes(options.isRemoveFormAttributes());
		compressor.setRemoveInputAttributes(options.isRemoveInputAttributes());
		compressor.setSimpleBooleanAttributes(options.isSimpleBooleanAttributes());
		compressor.setRemoveJavaScriptProtocol(options.isRemoveJavaScriptProtocol());
		compressor.setRemoveHttpProtocol(options.isRemoveHttpProtocol());
		compressor.setRemoveHttpsProtocol(options.isRemoveHttpsProtocol());
		compressor.setPreserveLineBreaks(options.isPreserveLineBreaks());
		this.compressor = compressor;
	}

	HtmlCompressor compressor = null;
	
	@Override
	public String compress(String filePath, String srcContent) {
		try {
			//			long start = System.currentTimeMillis();
			String ret = compressor.compress(srcContent);
			//			long end = System.currentTimeMillis();
			//			System.out.println("html压缩耗时" + (end - start) + "ms");

			if (ret == null || ret.length() == 0) {
				log.warn("压缩后的文件大小为0, {}", filePath);
				return srcContent;
			}

			//			byte[] initBytes = srcContent.getBytes();
			//			byte[] afterBytes = ret.getBytes();
			//
			//			if (afterBytes.length >= initBytes.length) {
			//				log.warn("HTML压缩后的文件反而较大,  init size:{}, after size:{}, file:{}", initBytes.length, afterBytes.length, filePath);
			//				return srcContent;
			//			}

//			return commits + ret;
			return ret;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return srcContent;
		}
	}

	//	public static void setOptions(HtmlOptions options) {
	//		TioHtmlCompressor.options = options;
	//	}

	/**
	 * @param args
	 * @author tanyaowu
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		byte[] bytes = Files.readAllBytes(new File("E:\\\\svn\\\\nbyb\\\\html\\\\nbyb_bootstrap\\\\dist\\\\index.html").toPath());
		String xx = new String(bytes, "utf-8");
		//		String xx = FileUtil.readString(new File("E:\\svn\\nbyb\\html\\nbyb_bootstrap\\dist\\index.html"), "utf-8");
		String compiled_code = TioHtmlCompressor.me.compress("xx.html", xx);
		System.out.println(compiled_code);
		long end = System.currentTimeMillis();
		long iv = end - start;
		System.out.println("耗时:" + iv + "ms");
	}
}
