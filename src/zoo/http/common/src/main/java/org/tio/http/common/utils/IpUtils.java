package org.tio.http.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.utils.hutool.StrUtil;

/**
 *
 * @author tanyaowu
 * 2017年8月10日 下午5:05:49
 */
public class IpUtils {
	private static Logger log = LoggerFactory.getLogger(IpUtils.class);

	/**
	 * 获取本机 ip
	 * @return 本机IP
	 */
	public static String getLocalIp() throws SocketException {
		String localip = null; // 本地IP，如果没有配置外网IP则返回
		String netip = null; // 外网IP

		Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		boolean finded = false; // 是否找到外网IP
		while (netInterfaces.hasMoreElements() && !finded) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
					netip = ip.getHostAddress();
					finded = true;
					break;
				} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
					localip = ip.getHostAddress();
				}
			}
		}

		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}

	/**
	 * 如果是被代理了，获取客户端ip时，依次从下面这些头部中获取
	 */
	private final static String[] HEADER_NAMES_FOR_REALIP = new String[] { "x-forwarded-for", "proxy-client-ip", "wl-proxy-client-ip", "x-real-ip" };

	/**
	 * 
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	public static String getRealIp(HttpRequest request) {
//		return getRealIp(request.channelContext, request.httpConfig, request.getHeaders());
		
		if (request.httpConfig == null) {
			return request.getRemote().getIp();
		}

		if (request.httpConfig.isProxied()) {
			String headerName = null;
			String ip = null;
			for (String name : HEADER_NAMES_FOR_REALIP) {
				headerName = name;
				ip = request.getHeader(headerName);

				if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
					break;
				}
			}

			if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				headerName = null;
				ip = request.getRemote().getIp();
			}

			if (ip.contains(",")) {
				log.error("ip[{}], header name:{}", ip, headerName);
				ip = ip.split(",")[0].trim();
			}
			return ip;
		} else {
			return request.getRemote().getIp();
		}
	}

	/**
	 * 获取真实ip
	 * @param channelContext
	 * @param httpConfig
	 * @param httpHeaders
	 * @return
	 * @author tanyaowu
	 */
	public static String getRealIp(ChannelContext channelContext, HttpConfig httpConfig, Map<String, String> httpHeaders) {
		if (httpConfig == null) {
			return channelContext.getClientNode().getIp();
		}

		if (httpConfig.isProxied()) {
			String headerName = null;
			String ip = null;
			for (String name : HEADER_NAMES_FOR_REALIP) {
				headerName = name;
				ip = httpHeaders.get(headerName);

				if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
					break;
				}
			}

			if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				headerName = null;
				ip = channelContext.getClientNode().getIp();
			}

			if (ip.contains(",")) {
				if (log.isInfoEnabled()) {
					log.info("ip[{}], header name:{}", ip, headerName);

				}
				ip = ip.split(",")[0].trim();
			}
			return ip;
		} else {
			return channelContext.getClientNode().getIp();
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isIp(String str) {
		if (str.length() < 7 || str.length() > 15 || "".equals(str)) {
			return false;
		}
		/** 
		 * 判断IP格式和范围 
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(str);
		boolean ipAddress = mat.find();
		return ipAddress;
	}

}
