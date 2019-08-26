/**
 * 
 */
package org.tio.webpack.compress.js;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.SysConst;
import org.tio.webpack.compress.ResCompressor;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * @author tanyaowu
 *
 */
public class YuiJsCompressor implements ResCompressor {
	private static Logger log = LoggerFactory.getLogger(YuiJsCompressor.class);

	public static YuiJsCompressor ME = new YuiJsCompressor();

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File initFile = new File("D:\\svn_nb\\nbyb\\html\\nbyb\\web_server\\src\\res\\js\\live\\live-all.txt");
		byte[] bytes = Files.readAllBytes(initFile.toPath());
		String content = new String(bytes, "utf-8");
		//		String content = cn.hutool.core.io.FileUtil.readString(initFile, "utf-8");

		String xx = YuiJsCompressor.ME.compress(initFile.getAbsolutePath(), content);
		System.out.println(xx);
	}

	/**
	 * 
	 */
	private YuiJsCompressor() {

	}

	@Override
	public String compress(String filePath, String srcContent) {
		ByteArrayInputStream input = null;
		input = new ByteArrayInputStream(srcContent.getBytes(SysConst.DEFAULT_CHARSET));
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			transform(filePath, input, output);
		} catch (IOException e) {
			log.error(filePath, e);
			return srcContent;
		}
		byte[] bs = output.toByteArray();
		return new String(bs, SysConst.DEFAULT_CHARSET);
	}

	/**
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public void transform(String filePath, InputStream input, OutputStream output) throws IOException {
		Reader reader = new InputStreamReader(input, SysConst.DEFAULT_CHARSET);
		JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new ErrorReporter() {

			@Override
			public void error(String arg0, String arg1, int arg2, String arg3, int arg4) {
				log.error("file:{}, arg0:{}, arg1:{}, arg2:{}, arg3:{}, arg4:{}", filePath, arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4) {
				log.error("file:{}, arg0:{}, arg1:{}, arg2:{}, arg3:{}, arg4:{}", filePath, arg0, arg1, arg2, arg3, arg4);
				return null;
			}

			@Override
			public void warning(String arg0, String arg1, int arg2, String arg3, int arg4) {
				log.error("file:{}, arg0:{}, arg1:{}, arg2:{}, arg3:{}, arg4:{}", filePath, arg0, arg1, arg2, arg3, arg4);

			}
		});

		// write compressed output 
		OutputStreamWriter writer = new OutputStreamWriter(output, SysConst.DEFAULT_CHARSET);
		/**
		 * Writer out, int linebreak, boolean munge, boolean verbose,
		    boolean preserveAllSemiColons, boolean disableOptimizations
		 */
		boolean verbose = false;
		compressor.compress(writer, 100000, false, verbose, true, true);
		writer.flush();
	}

}
