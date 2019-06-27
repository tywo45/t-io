package org.tio.utils.jfinal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.SysConst;

/**
 * 
 * @author tanyaowu
 */
public class Prop {
	private static Logger log = LoggerFactory.getLogger(Prop.class);

	private Properties properties = null;

	/**
	 * 
	 * @param fileName
	 */
	public Prop(String fileName) {
		this(fileName, SysConst.DEFAULT_ENCODING);
	}

	/**
	 * 
	 * @param fileName
	 * @param encoding
	 */
	public Prop(String fileName, String encoding) {
		this(fileName, Charset.forName(encoding));
	}

	/**
	 * 
	 * @param fileName
	 * @param charset
	 */
	public Prop(String fileName, Charset charset) {
		InputStream inputStream = null;
		try {
			inputStream = getClassLoader().getResourceAsStream(fileName); // properties.load(Prop.class.getResourceAsStream(fileName));
			if (inputStream == null) {
				throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
			}
			properties = new Properties();
			properties.load(new InputStreamReader(inputStream, charset));
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties file.", e);
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
		}
	}

	private ClassLoader getClassLoader() {
		ClassLoader ret = Thread.currentThread().getContextClassLoader();
		return ret != null ? ret : getClass().getClassLoader();
	}

	/**
	 * Prop constructor.
	 * @see #Prop(File, String)
	 */
	public Prop(File file) {
		this(file, SysConst.DEFAULT_ENCODING);
	}

	/**
	 * 
	 * @param file
	 * @param encoding
	 */
	public Prop(File file, String encoding) {
		this(file, Charset.forName(encoding));
	}

	/**
	 * 
	 * @param file
	 * @param charset
	 */
	public Prop(File file, Charset charset) {
		if (file == null) {
			throw new IllegalArgumentException("File can not be null.");
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("File not found : " + file.getName());
		}

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			properties = new Properties();
			properties.load(new InputStreamReader(inputStream, charset));
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties file.", e);
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
		}
	}

	/**
	 * 
	 * @param prop
	 * @return
	 */
	public Prop append(Prop prop) {
		if (prop == null) {
			throw new IllegalArgumentException("prop can not be null");
		}
		properties.putAll(prop.getProperties());
		return this;
	}

	public Prop append(String fileName, String encoding) {
		return append(new Prop(fileName, encoding));
	}

	public Prop append(String fileName, Charset charset) {
		return append(new Prop(fileName, charset));
	}

	public Prop append(String fileName) {
		return append(fileName, SysConst.DEFAULT_CHARSET);
	}

	public Prop appendIfExists(String fileName, String encoding) {
		try {
			return append(new Prop(fileName, encoding));
		} catch (Exception e) {
			return this;
		}
	}

	public Prop appendIfExists(String fileName) {
		return appendIfExists(fileName, SysConst.DEFAULT_ENCODING);
	}

	public Prop append(File file, String encoding) {
		return append(new Prop(file, encoding));
	}

	public Prop append(File file) {
		return append(file, SysConst.DEFAULT_ENCODING);
	}

	public Prop appendIfExists(File file, String encoding) {
		if (file.exists()) {
			append(new Prop(file, encoding));
		}
		return this;
	}

	public Prop appendIfExists(File file) {
		return appendIfExists(file, SysConst.DEFAULT_ENCODING);
	}

	public String get(String key) {
		return properties.getProperty(key);
	}

	public String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public Integer getInt(String key) {
		return getInt(key, null);
	}

	public Integer getInt(String key, Integer defaultValue) {
		String value = properties.getProperty(key);
		if (value != null) {
			return Integer.parseInt(value.trim());
		}
		return defaultValue;
	}

	public Long getLong(String key) {
		return getLong(key, null);
	}

	public Long getLong(String key, Long defaultValue) {
		String value = properties.getProperty(key);
		if (value != null) {
			return Long.parseLong(value.trim());
		}
		return defaultValue;
	}

	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		String value = properties.getProperty(key);
		if (value != null) {
			value = value.toLowerCase().trim();
			if ("true".equals(value)) {
				return true;
			} else if ("false".equals(value)) {
				return false;
			}
			throw new RuntimeException("The value can not parse to Boolean : " + value);
		}
		return defaultValue;
	}

	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

	public Properties getProperties() {
		return properties;
	}
}
