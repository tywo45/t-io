package org.tio.core.udp;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Node;
import org.tio.core.udp.task.UdpSendRunnable;
import org.tio.utils.SystemTimer;
import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu
 * 2017年7月5日 下午2:54:12
 */
public class UdpClient {
	private static Logger log = LoggerFactory.getLogger(UdpClient.class);

	//	private static final int TIMEOUT = 5000; //设置接收数据的超时时间

	public static void main(String[] args) {
		UdpClientConf udpClientConf = new UdpClientConf("127.0.0.1", 3000, 5000);
		UdpClient udpClient = new UdpClient(udpClientConf);
		udpClient.start();

		long start = SystemTimer.currTime;
		String x = "[{\"collectTime\":\"2019-06-06 11:24:08\",\"TimeWritten\":\"20190605133513.027808-000\",\"Category\":\"12544\",\"EventIdentifier\":\"4624\",\"TimeGenerated\":\"20190605133513.027808-000\",\"User\":\"\",\"Message\":\"已成功登录帐户。主题:\\t安全 ID:\\t\\tS-1-5-18\\t\\t帐户名:\\t\\tLWT7R8I9D0QC0VM$\\t帐户域:\\t\\tWorkGroup\\t登录 ID:\\t\\t0x3e7登录类型:\\t\\t\\t5新登录:\\t安全 ID:\\t\\tS-1-5-18\\t帐户名:\\t\\tSYSTEM\\t帐户域:\\t\\tNT AUTHORITY\\t登录 ID:\\t\\t0x3e7\\t登录 GUID:\\t\\t{00000000-0000-0000-0000-000000000000}进程信息:\\t进程 ID:\\t\\t0x2c8\\t进程名:\\t\\tC:\\\\Windows\\\\System32\\\\services.exe网络信息:\\t工作站名:\\t\\t源网络地址:\\t-\\t源端口:\\t\\t-详细身份验证信息:\\t登录进程:\\t\\tAdvapi  \\t身份验证数据包:\\tNegotiate\\t传递服务:\\t-\\t数据包名(仅限 NTLM):\\t-\\t密钥长度:\\t\\t0在创建登录会话后在被访问的计算机上生成此事件。“主题”字段指明本地系统上请求登录的帐户。这通常是一个服务(例如 Server 服务)或本地进程(例如 Winlogon.exe 或 Services.exe)。“登录类型”字段指明发生的登录种类。最常见的类型是 2 (交互式)和 3 (网络)。“新登录”字段会指明新登录是为哪个帐户创建的，即登录的帐户。“网络”字段指明远程登录请求来自哪里。“工作站名”并非总是可用，而且在某些情况下可能会留为空白。“身份验证信息”字段提供关于此特定登录请求的详细信息。\\t-“登录 GUID”是可以用于将此事件与一个 KDC 事件关联起来的唯一标识符。\\t-“传递服务”指明哪些直接服务参与了此登录请求。\\t- “数据包名”指明在 NTLM 协议之间使用了哪些子协议。\\t-“密钥长度”指明生成的会话密钥的长度。如果没有请求会话密钥则此字段为 0。\",\"EventType\":\"4\",\"dataMessages\":\"\",\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"Data\":\"\",\"EventCode\":\"4624\",\"dataFeatures\":\"Log\",\"Type\":\"登录成功\",\"metricCodes\":\"s_001\",\"ComputerName\":\"LWT7R8I9D0QC0VM\",\"InsertionStrings\":\"{\\\"S-1-5-18\\\",\\\"LWT7R8I9D0QC0VM$\\\",\\\"WorkGroup\\\",\\\"0x3e7\\\",\\\"S-1-5-18\\\",\\\"SYSTEM\\\",\\\"NT AUTHORITY\\\",\\\"0x3e7\\\",\\\"5\\\",\\\"Advapi  \\\",\\\"Negotiate\\\",\\\"\\\",\\\"{00000000-0000-0000-0000-000000000000}\\\",\\\"-\\\",\\\"-\\\",\\\"0\\\",\\\"0x2c8\\\",\\\"C:\\\\Windows\\\\System32\\\\services.exe\\\",\\\"-\\\",\\\"-\\\"}\",\"CategoryString\":\"登录\",\"RecordNumber\":\"2682\",\"Logfile\":\"Security\"}, {\"collectTime\":\"2019-06-06 11:24:08\",\"TimeWritten\":\"20190605122905.563801-000\",\"Category\":\"12544\",\"EventIdentifier\":\"4624\",\"TimeGenerated\":\"20190605122905.563801-000\",\"User\":\"\",\"Message\":\"已成功登录帐户。主题:\\t安全 ID:\\t\\tS-1-5-18\\t\\t帐户名:\\t\\tLWT7R8I9D0QC0VM$\\t帐户域:\\t\\tWorkGroup\\t登录 ID:\\t\\t0x3e7登录类型:\\t\\t\\t5新登录:\\t安全 ID:\\t\\tS-1-5-18\\t帐户名:\\t\\tSYSTEM\\t帐户域:\\t\\tNT AUTHORITY\\t登录 ID:\\t\\t0x3e7\\t登录 GUID:\\t\\t{00000000-0000-0000-0000-000000000000}进程信息:\\t进程 ID:\\t\\t0x2c8\\t进程名:\\t\\tC:\\\\Windows\\\\System32\\\\services.exe网络信息:\\t工作站名:\\t\\t源网络地址:\\t-\\t源端口:\\t\\t-详细身份验证信息:\\t登录进程:\\t\\tAdvapi  \\t身份验证数据包:\\tNegotiate\\t传递服务:\\t-\\t数据包名(仅限 NTLM):\\t-\\t密钥长度:\\t\\t0在创建登录会话后在被访问的计算机上生成此事件。“主题”字段指明本地系统上请求登录的帐户。这通常是一个服务(例如 Server 服务)或本地进程(例如 Winlogon.exe 或 Services.exe)。“登录类型”字段指明发生的登录种类。最常见的类型是 2 (交互式)和 3 (网络)。“新登录”字段会指明新登录是为哪个帐户创建的，即登录的帐户。“网络”字段指明远程登录请求来自哪里。“工作站名”并非总是可用，而且在某些情况下可能会留为空白。“身份验证信息”字段提供关于此特定登录请求的详细信息。\\t-“登录 GUID”是可以用于将此事件与一个 KDC 事件关联起来的唯一标识符。\\t-“传递服务”指明哪些直接服务参与了此登录请求。\\t- “数据包名”指明在 NTLM 协议之间使用了哪些子协议。\\t-“密钥长度”指明生成的会话密钥的长度。如果没有请求会话密钥则此字段为 0。\",\"EventType\":\"4\",\"dataMessages\":\"\",\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"Data\":\"\",\"EventCode\":\"4624\",\"dataFeatures\":\"Log\",\"Type\":\"登录成功\",\"metricCodes\":\"s_001\",\"ComputerName\":\"LWT7R8I9D0QC0VM\",\"InsertionStrings\":\"{\\\"S-1-5-18\\\",\\\"LWT7R8I9D0QC0VM$\\\",\\\"WorkGroup\\\",\\\"0x3e7\\\",\\\"S-1-5-18\\\",\\\"SYSTEM\\\",\\\"NT AUTHORITY\\\",\\\"0x3e7\\\",\\\"5\\\",\\\"Advapi  \\\",\\\"Negotiate\\\",\\\"\\\",\\\"{00000000-0000-0000-0000-000000000000}\\\",\\\"-\\\",\\\"-\\\",\\\"0\\\",\\\"0x2c8\\\",\\\"C:\\\\Windows\\\\System32\\\\services.exe\\\",\\\"-\\\",\\\"-\\\"}\",\"CategoryString\":\"登录\",\"RecordNumber\":\"2680\",\"Logfile\":\"Security\"}]";
		for (int i = 0; i < 2; i++) {
			String str = i + "、" + x;
			udpClient.send(str.getBytes());
		}
		long end = SystemTimer.currTime;
		long iv = end - start;
		System.out.println("耗时:" + iv + "ms");
	}

	private LinkedBlockingQueue<DatagramPacket> queue = new LinkedBlockingQueue<>();

	private UdpClientConf udpClientConf = null;

	/**
	 * 服务器地址
	 */
	private InetSocketAddress inetSocketAddress = null;

	private UdpSendRunnable udpSendRunnable = null;

	public UdpClient(UdpClientConf udpClientConf) {
		super();
		this.udpClientConf = udpClientConf;
		Node node = this.udpClientConf.getServerNode();
		inetSocketAddress = new InetSocketAddress(node.getIp(), node.getPort());
		udpSendRunnable = new UdpSendRunnable(queue, udpClientConf, null);
	}

	public void send(byte[] data) {
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetSocketAddress);
		queue.add(datagramPacket);
	}

	public void send(String str) {
		send(str, null);
	}

	public void send(String data, String charset) {
		if (StrUtil.isBlank(data)) {
			return;
		}
		try {
			if (StrUtil.isBlank(charset)) {
				charset = udpClientConf.getCharset();
			}
			byte[] bs = data.getBytes(charset);
			send(bs);
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
	}

	public void start() {
		Thread thread = new Thread(udpSendRunnable, "tio-udp-client-send");
		thread.setDaemon(false);
		thread.start();
	}
}
