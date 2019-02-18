/**
 * 
 */
package org.tio.utils;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgentAnalyzer.UserAgentAnalyzerBuilder;
import nl.basjes.parse.useragent.classify.UserAgentClassifier;

/**
 * User-Agent工具类
 * @author tanyaowu
 *
 */
@SuppressWarnings("rawtypes")
public class UAUtils {

	private static UserAgentAnalyzer ua = null;

	static {//agent_name agent_version_major operating_system_name operating_system_version
		UserAgentAnalyzerBuilder<? extends UserAgentAnalyzer, ? extends UserAgentAnalyzerBuilder> builder = UserAgentAnalyzer.newBuilder();
		builder.withField(UserAgent.AGENT_NAME);
		builder.withField(UserAgent.AGENT_VERSION_MAJOR);

		builder.withField(UserAgent.OPERATING_SYSTEM_NAME);
		builder.withField(UserAgent.OPERATING_SYSTEM_VERSION);
		//		
		builder.withField(UserAgent.DEVICE_CLASS);

		builder.hideMatcherLoadStats();
		builder.withCache(25000);
		builder.withUserAgentMaxLength(1024);

		ua = builder.build();
	}

	/**
	 * 
	 */
	public UAUtils() {

	}

	/**
	 * 
	 * @param userAgentString
	 * @return
	 */
	public static UserAgent parse(String userAgentString) {
		return ua.parse(userAgentString);
	}

	/**
	 * 是否是
	 * @param userAgent
	 * @return
	 * @author tanyaowu
	 */
	public static boolean isMobile(UserAgent userAgent) {
		return UserAgentClassifier.isMobile(userAgent);
	}

}
