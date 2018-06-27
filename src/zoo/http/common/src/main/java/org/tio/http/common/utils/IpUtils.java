package org.tio.http.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;

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
	 * 
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	public static String getRealIp(HttpRequest request) {
		HttpConfig httpConfig = request.getHttpConfig();
		if (httpConfig == null) {
			return request.getRemote().getIp();
		}

		if (httpConfig.isProxied()) {
			String ip = request.getHeader("x-forwarded-for");
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("proxy-client-ip");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("wl-proxy-client-ip");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemote().getIp();
			}

			if (StringUtils.contains(ip, ",")) {
				log.error("ip[{}]", ip);
				ip = StringUtils.split(ip, ",")[0].trim();
			}
			return ip;
		} else {
			return request.getRemote().getIp();
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
	
	  public static void main(String[] args)   
	    {  
	        /** 
	         * 符合IP地址的范围 
	         */  
	         String oneAddress = "10.43.30.45";  
	         /** 
	         * 符合IP地址的长度范围但是不符合格式 
	         */  
	         String twoAddress = "127.30.45";  
	         /** 
	         * 不符合IP地址的长度范围 
	         */  
	         String threeAddress = "7.0.4";  
	         /** 
	         * 不符合IP地址的长度范围但是不符合IP取值范围 
	         */  
	         String fourAddress = "255.155.255.3e";  

	        

	         //判断oneAddress是否是IP  
	         System.out.println(isIp(oneAddress));  

	         //判断twoAddress是否是IP  
	         System.out.println(isIp(twoAddress));  

	         //判断threeAddress是否是IP  
	         System.out.println(isIp(threeAddress));  

	         //判断fourAddress是否是IP  
	         System.out.println(isIp(fourAddress));  
	    }  
}
