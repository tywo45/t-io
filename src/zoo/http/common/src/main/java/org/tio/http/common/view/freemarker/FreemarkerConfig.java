package org.tio.http.common.view.freemarker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.view.ModelGenerator;
import org.tio.utils.freemarker.ShortMessageTemplateExceptionHandler;

import freemarker.template.Configuration;

/**
 * @author tanyaowu 
 * 2017年11月15日 下午1:11:55
 */
public class FreemarkerConfig {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(FreemarkerConfig.class);

	private Configuration configuration;

	private HttpConfig httpConfig;

	/**
	 * key: 请求过来的domain，形如：www.t-io.org
	 * value: Configuration对象
	 */
	private volatile Map<String, Configuration> domainConfMap = null;

	private ModelGenerator modelGenerator;

	private String[] suffixes = null;

	private ConfigurationCreater configurationCreater;

	public FreemarkerConfig(HttpConfig httpConfig, ModelGenerator modelGenerator, String[] suffixes, ConfigurationCreater configurationCreater) throws IOException {
		super();
		this.configurationCreater = configurationCreater;

		String pageRoot = httpConfig.getPageRoot();
		if (pageRoot == null) {
			throw new IOException("没有配置pageRoot");
		}

		httpConfig.setFreemarkerConfig(this);

		this.httpConfig = httpConfig;
		this.modelGenerator = modelGenerator;
		this.setSuffixes(suffixes);

		this.configuration = createConfiguration(httpConfig, pageRoot);

		Map<String, String> domainPageMap = httpConfig.getDomainPageMap();
		if (domainPageMap != null && domainPageMap.size() > 0) {
			Set<Entry<String, String>> set = domainPageMap.entrySet();
			for (Entry<String, String> entry : set) {
				String domain = entry.getKey();
				String file = entry.getValue();
				//				Configuration cfg = createConfiguration(httpConfig, file);
				addDomainConfiguration(domain, file);
			}
		}
	}

	/**
	 * 
	 * @param httpConfig
	 * @param root
	 * @return
	 * @throws IOException
	 */
	private Configuration createConfiguration(HttpConfig httpConfig, String root) throws IOException {
		if (httpConfig.getPageRoot() == null) {
			return null;
		}

		if (configurationCreater != null) {
			return configurationCreater.createConfiguration(httpConfig, root);
		}

		Configuration cfg = new Configuration(Configuration.getVersion());

		if (httpConfig.isPageInClasspath()) {
			cfg.setClassForTemplateLoading(this.getClass(), "/" + root/**.substring("classpath:".length())*/
			);
			//cfg.setClassForTemplateLoading(FreemarkerUtil.class, "/template");
		} else {
			cfg.setDirectoryForTemplateLoading(new File(root));
		}

		cfg.setDefaultEncoding(httpConfig.getCharset());
		//		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);
		cfg.setTemplateExceptionHandler(ShortMessageTemplateExceptionHandler.me);
		cfg.setLocale(Locale.SIMPLIFIED_CHINESE);
		return cfg;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public Configuration getConfiguration(HttpRequest request) {
		if (this.domainConfMap == null || domainConfMap.size() == 0) {
			return configuration;
		}

		String domain = request.getDomain();
		Configuration root = domainConfMap.get(domain);
		if (root != null) {
			return root;
		}

		Set<Entry<String, Configuration>> set = domainConfMap.entrySet();
		for (Entry<String, Configuration> entry : set) {
			String d = entry.getKey();
			if (d.startsWith(".") && domain.endsWith(d)) {
				Configuration cfg = entry.getValue();
				domainConfMap.put(domain, cfg);
				return cfg;
			}
		}

		domainConfMap.put(domain, configuration);
		return configuration;
	}

	//	/**
	//	 * 
	//	 * @param domain 形如www.t-io.org的域名，也可以是形如.t-io.org这样的通配域名
	//	 * @param pageRoot 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	//	 * @throws IOException 
	//	 */
	//	public void addDomainConfiguration(String domain, String pageRoot) throws IOException {
	//		addDomainConfiguration(domain, HttpConfig.fromPath(pageRoot));
	//	}

	/**
	 * 
	 * @param domain
	 * @param pageRoot
	 * @throws IOException
	 */
	public void addDomainConfiguration(String domain, String pageRoot) throws IOException {
		if (domainConfMap == null) {
			synchronized (this) {
				if (domainConfMap == null) {
					domainConfMap = new HashMap<>();
				}
			}
		}

		Configuration configuration = createConfiguration(httpConfig, pageRoot);
		domainConfMap.put(domain, configuration);
	}

	//	public Configuration getConfiguration() {
	//		return configuration;
	//	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public ModelGenerator getModelGenerator() {
		return modelGenerator;
	}

	public void setModelGenerator(ModelGenerator modelGenerator) {
		this.modelGenerator = modelGenerator;
	}

	/**
	 * 
	 * @author tanyaowu
	 */
	@SuppressWarnings("unused")
	private FreemarkerConfig() {
	}

	/**
	 * @return the suffixes
	 */
	public String[] getSuffixes() {
		return suffixes;
	}

	/**
	 * @param suffixes the suffixes to set
	 */
	public void setSuffixes(String[] suffixes) {
		this.suffixes = suffixes;
	}
}
