package org.tio.webpack.compress.js;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.webpack.compress.ResCompressor;

import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.SourceFile;

/**
 * An example of how to call the Closure Compiler programmatically, 
 * this is LICENSED AS GPL-3.0.
 *
 * @author edgar.factorial@gmail.com (Edgar Aroutiounian)
 */

public class TioJsCompressor implements ResCompressor {
	private static Logger log = LoggerFactory.getLogger(TioJsCompressor.class);

	public static final TioJsCompressor ME = new TioJsCompressor();

	/**
	 * 
	 * @author tanyaowu
	 */
	public TioJsCompressor() {
	}

	public static void init() {
	}

	static {

	}

	public String compress(String srcFilePath, String srcFileContent) {
		return compress(srcFilePath, srcFileContent, LanguageMode.ECMASCRIPT5, CompilationLevel.SIMPLE_OPTIMIZATIONS);
		//		return compress(srcFilePath, srcFileContent, CompilationLevel.WHITESPACE_ONLY);
	}

	static final String commits = "/*" + DOC + "*/\r\n";

	/**
	 * 
	 * @param srcFilePath 源文件路径
	 * @param srcFileContent 源文件内容
	 * @return
	 * @author tanyaowu
	 */
	public static String compress(String srcFilePath, String srcFileContent, LanguageMode languageMode, CompilationLevel compilationLevel) {

		// See :
		// closure-compiler/src/com/google/javascript/jscomp/CompilerOptions.java
		// lines 2864-2896

		//		options.setLanguageOut(LanguageMode.ECMASCRIPT5_STRICT);

		try {

			Compiler compiler = new Compiler();

			CompilerOptions options = new CompilerOptions();

			options.setLanguageIn(languageMode);
			options.setLanguageOut(LanguageMode.ECMASCRIPT5);
			options.setOutputCharset(Charset.forName(CHARSET));
			List<SourceFile> list = null;

			compilationLevel.setOptionsForCompilationLevel(options);

			try {
				list = CommandLineRunner.getBuiltinExterns(CompilerOptions.Environment.BROWSER);
			} catch (IOException e) {
				log.error("Exception raised", e);
			}

			list.add(SourceFile.fromCode(srcFilePath, srcFileContent));
			//		list.add(SourceFile.fromCode("x_"+filePath, fileContent));
			//		list.add(SourceFile.fromCode("xx.js", code));

			List<SourceFile> externs = new ArrayList<SourceFile>();

			compiler.compile(externs, list, options);
			String ret = compiler.toSource();
			if (ret == null || ret.length() == 0) {
				if (Objects.equals(languageMode, LanguageMode.ECMASCRIPT5)) {
					log.warn("用{}语言模式压缩后的文件大小为0，换ECMASCRIPT_NEXT试试, {}", languageMode, srcFilePath);
					return compress(srcFilePath, srcFileContent, LanguageMode.ECMASCRIPT_NEXT, CompilationLevel.SIMPLE_OPTIMIZATIONS);
				}

				if (Objects.equals(compilationLevel, CompilationLevel.ADVANCED_OPTIMIZATIONS)) {
					log.warn("用{}压缩后的文件大小为0，换SIMPLE_OPTIMIZATIONS试试, {}", compilationLevel, srcFilePath);
					return compress(srcFilePath, srcFileContent, languageMode, CompilationLevel.SIMPLE_OPTIMIZATIONS);
				} else if (Objects.equals(compilationLevel, CompilationLevel.SIMPLE_OPTIMIZATIONS)) {
					log.warn("用{}压缩后的文件大小为0，换WHITESPACE_ONLY试试, {}", compilationLevel, srcFilePath);
					return compress(srcFilePath, srcFileContent, languageMode, CompilationLevel.WHITESPACE_ONLY);
				} else if (Objects.equals(compilationLevel, CompilationLevel.WHITESPACE_ONLY)) {
					log.warn("用{}压缩后的文件大小为0，换BUNDLE试试, {}", compilationLevel, srcFilePath);
					return compress(srcFilePath, srcFileContent, languageMode, CompilationLevel.BUNDLE);
				}

				log.error("用{}压缩后的文件大小为0，没救了就这样吧, {}", compilationLevel, srcFilePath);
				return srcFileContent;
			}

			//			byte[] initBytes = srcFileContent.getBytes();
			//			byte[] afterBytes = ret.getBytes();
			//
			//			if (afterBytes.length >= initBytes.length) {
			//				log.warn("压缩后的文件反而较大,  init size:{}, after size:{}, file:{}", initBytes.length, afterBytes.length, srcFilePath);
			//				return srcFileContent;
			//			}

			return commits + ret;
		} catch (Exception e) {
			log.error("压缩" + srcFilePath + "时产生异常", e);
			return srcFileContent;
		}
	}

	/**
	 * @param args
	 * @author tanyaowu
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		try {
			long start = System.currentTimeMillis();

			int i = 0;
			String filePath = "d:/x" + i++ + ".js";
			//		String content = FileUtil.readString(new File("E:\\svn\\nbyb\\html\\nbyb_bootstrap\\src\\public\\js\\jquery-3.2.1.min.js"), "utf-8");
			//			File initFile = new File("D:\\svn_nb\\nbyb\\html\\nbyb\\web_server\\src\\res\\public\\js\\obs\\obs-websocket.js");
			File initFile = new File("D:\\svn_nb\\nbyb\\html\\nbyb\\web_server\\src\\res\\js\\live\\live-all.txt");
			//			File initFile = new File("D:\\svn_nb\\nbyb\\html\\nbyb\\web_server\\src\\res\\public\\js\\validate\\talent-validate-all.js");

			byte[] bytes = Files.readAllBytes(initFile.toPath());
			String content = new String(bytes, "utf-8");
			//			String content = FileUtil.readString(initFile, "utf-8");
			String compiled_code = TioJsCompressor.ME.compress(initFile.getAbsolutePath(), content);
			System.out.println(compiled_code);
			Files.write(Paths.get(filePath), compiled_code.getBytes("utf-8"));
			//			FileUtil.writeString(compiled_code, filePath, "utf-8");
			long end = System.currentTimeMillis();
			long iv = end - start;
			System.out.println("耗时:" + iv + "ms");
			//
			//		start = System.currentTimeMillis();
			//		filePath = "d:/x" + i++ + ".js";
			//		compiled_code = compress("2", "//注释var c = 1 + 2\r\nvar d = 1 + 2\r\n gotoxx(c)");
			//		System.out.println(compiled_code);
			//		end = System.currentTimeMillis();
			//		iv = end - start;
			//		System.out.println("耗时:" + iv + "ms");
			//
			//		start = System.currentTimeMillis();
			//		filePath = "d:/x" + i + ".js";
			//		compiled_code = compress("2", "//注释var c = 1 + 2\r\nvar d = 1 + 2\r\n gotoxx(c)");
			//		System.out.println(compiled_code);
			//		end = System.currentTimeMillis();
			//		iv = end - start;
			//		System.out.println("耗时:" + iv + "ms");

			//
			//		filePath = "d:/x" + i++ + ".js";
			//		compiled_code = compress(filePath, "var a = 1 + 2\r\nvar b = 1 + 2\r\n gotoxx(a)");
			//		System.out.println(compiled_code);
			//
			//		filePath = "d:/x" + i++ + ".js";
			//		compiled_code = compress(filePath, "var a = 1 + 2\r\nvar b = 1 + 2\r\n gotoxx(pol)");
			//		System.out.println(compiled_code);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
}
