package org.tio.utils.freemarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileLock;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * 这个代码在不违背开源协议的前提下摘自：https://gitee.com/sanluan/PublicCMS-preview/blob/master/publiccms-parent/publiccms-common/src/main/java/com/publiccms/common/tools/FreeMarkerUtils.java 
 * 感谢publiccms作者张向东同志^_^
 * @author tanyaowu 
 * 2017年11月13日 下午3:09:21
 */
public class FreemarkerUtils {
	private static Logger log = LoggerFactory.getLogger(FreemarkerUtils.class);

	public static final String DEFAULT_CHARSET = "utf-8";

	/**
	 * 
	 * @author tanyaowu
	 */
	public FreemarkerUtils() {
	}

	/**
	* @param templateFilePath
	* @param destFilePath
	* @param configuration
	* @param model
	* @throws IOException
	* @throws TemplateException
	*/
	public static void generateFileByFile(String templateFilePath, String destFilePath, Configuration configuration, Object model) throws IOException, TemplateException {
		generateFileByFile(templateFilePath, destFilePath, configuration, model, true, false);
	}

	/**
	 * @param templateFilePath
	 * @param destFilePath
	 * @param configuration
	 * @param model
	 * @param override
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static void generateFileByFile(String templateFilePath, String destFilePath, Configuration configuration, Object model, boolean override)
	        throws IOException, TemplateException {
		generateFileByFile(templateFilePath, destFilePath, configuration, model, override, false);
	}

	/**
	 * @param templateFilePath
	 * @param destFilePath
	 * @param configuration
	 * @param model
	 * @param override
	 * @param append
	 * @throws ParseException
	 * @throws MalformedTemplateNameException
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static void generateFileByFile(String templateFilePath, String destFilePath, Configuration configuration, Object model, boolean override, boolean append)
	        throws MalformedTemplateNameException, ParseException, IOException, TemplateException {
		Template t = configuration.getTemplate(templateFilePath);
		File destFile = new File(destFilePath);
		if (override || append || !destFile.exists()) {
			File parent = destFile.getParentFile();
			if (null != parent) {
				parent.mkdirs();
			}
			try (FileOutputStream outputStream = new FileOutputStream(destFile, append); FileLock fileLock = outputStream.getChannel().tryLock();) {
				Writer out = new OutputStreamWriter(outputStream, DEFAULT_CHARSET);
				t.process(model, out);
			}
			log.info(destFilePath + "    saved!");
		} else {
			log.error(destFilePath + "    already exists!");
		}
	}

	/**
	 * @param template
	 * @param configuration
	 * @return render result
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String generateStringByPath(String template, Configuration configuration) throws IOException, TemplateException {
		return generateStringByPath(template, configuration, new HashMap<String, Object>());
	}

	/**
	 * @param template
	 * @param configuration
	 * @param model
	 * @return render result
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String generateStringByPath(String template, Configuration configuration, Object model) throws IOException, TemplateException {
		StringWriter writer = new StringWriter();
		generateStringByPath(writer, template, configuration, model);
		return writer.toString();
	}

	/**
	 * @param writer
	 * @param template
	 * @param configuration
	 * @param model
	 * @throws TemplateNotFoundException
	 * @throws MalformedTemplateNameException
	 * @throws ParseException
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static void generateStringByPath(Writer writer, String template, Configuration configuration, Object model)
	        throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		Template tpl = configuration.getTemplate(template);
		tpl.process(model, writer);
	}

	/**
	 * @param templateContent
	 * @param configuration
	 * @param model
	 * @return render result
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String generateStringByString(String templateContent, Configuration configuration, Object model) throws IOException, TemplateException {
		Template tpl = new Template(null, templateContent, configuration);
		StringWriter writer = new StringWriter();
		tpl.process(model, writer);
		return writer.toString();
	}

}
