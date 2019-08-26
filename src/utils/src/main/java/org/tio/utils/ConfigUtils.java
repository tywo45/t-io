package org.tio.utils;

import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.DefaultReloadingDetectorFactory;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.hutool.StrUtil;

/**
 * 
 * @author tanyaowu 
 * 2016年9月14日 下午5:06:51
 */
public class ConfigUtils {
	private static Logger log = LoggerFactory.getLogger(ConfigUtils.class);

	/**
	 * 
	 * @param filename
	 * @param encoding
	 * @param t
	 * @return
	 * @throws FileNotFoundException
	 * @author: tanyaowu
	 */
	public static <T extends PropertiesConfiguration> T initConfig(String filename, String encoding, T t) throws FileNotFoundException {
		return initConfig(null, filename, encoding, t);
	}

	/**
	 * 
	 * @param filename
	 * @param encoding
	 * @return
	 * @throws FileNotFoundException
	 * @author: tanyaowu
	 */
	public static PropertiesConfiguration initConfig(String filename, String encoding) throws FileNotFoundException {
		return initConfig(null, filename, encoding);
	}

	/**
	 * 
	 * @param filename1
	 * @param filename2
	 * @param encoding
	 * @return
	 * @throws FileNotFoundException
	 * @author: tanyaowu
	 */
	public static PropertiesConfiguration initConfig(String filename1, String filename2, String encoding) throws FileNotFoundException {
		return initConfig(filename1, filename2, encoding, ',');
	}

	/**
	 * 
	 * @param filename1
	 * @param filename2
	 * @param encoding
	 * @param listDelimiter
	 * @return
	 * @throws FileNotFoundException
	 * @author: tanyaowu
	 */
	public static PropertiesConfiguration initConfig(String filename1, String filename2, String encoding, char listDelimiter) throws FileNotFoundException {
		Parameters _parameters = new Parameters();
		PropertiesBuilderParameters parameters = _parameters.properties();
		String filename = filename1;
		ClassLoader cl = ConfigUtils.class.getClassLoader();
		URL url = null;
		if (StrUtil.isNotBlank(filename1)) {
			url = (cl != null ? cl.getResource(filename1) : ClassLoader.getSystemResource(filename1));
		}

		if (url == null) {
			url = (cl != null ? cl.getResource(filename2) : ClassLoader.getSystemResource(filename2));
			if (url == null) {
				throw new FileNotFoundException(filename1);
			}
			filename = filename2;
		}

		parameters.setFileName(filename);
		parameters.setThrowExceptionOnMissing(false);
		parameters.setEncoding(encoding);
		parameters.setListDelimiterHandler(new DefaultListDelimiterHandler(listDelimiter));
		parameters.setReloadingDetectorFactory(new DefaultReloadingDetectorFactory());
		parameters.setIncludesAllowed(true);
		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(parameters);

		try {
			PropertiesConfiguration config = builder.getConfiguration();
			return config;
		} catch (ConfigurationException e) {
			log.error(e.toString(), e);
			return null;
		}
	}

	/**
	 * 如果finename1存在就加载finename1，否则加载finename2
	 * @param filename1
	 * @param filename2
	 * @param encoding
	 * @param t
	 * @return
	 * @throws FileNotFoundException
	 * @author: tanyaowu
	 */
	public static <T extends PropertiesConfiguration> T initConfig(String filename1, String filename2, String encoding, T t) throws FileNotFoundException {
		return initConfig(filename1, filename2, encoding, ',', t);
	}

	/**
	 * 如果finename1存在就加载finename1，否则加载finename2
	 * @param filename1
	 * @param filename2
	 * @param encoding
	 * @param listDelimiter
	 * @param t
	 * @return
	 * @throws FileNotFoundException
	 * @author: tanyaowu
	 */
	public static <T extends PropertiesConfiguration> T initConfig(String filename1, String filename2, String encoding, char listDelimiter, T t) throws FileNotFoundException {
		PropertiesConfiguration config = initConfig(filename1, filename2, encoding, listDelimiter);
		t.append(config);
		return t;
	}

}
