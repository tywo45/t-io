package org.tio.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Node;
import org.tio.utils.IoUtils;
import org.tio.utils.SysConst;
import org.tio.utils.hutool.DateUtil;
import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu
 *
 */
public class TioServer {
	private static Logger					log					= LoggerFactory.getLogger(TioServer.class);
	private ServerTioConfig				serverTioConfig;
	private AsynchronousServerSocketChannel	serverSocketChannel;
	private AsynchronousChannelGroup		channelGroup		= null;
	private Node							serverNode;
	private boolean							isWaitingStop		= false;
	private boolean							checkLastVersion	= true;

	/**
	 *
	 * @param serverTioConfig
	 *
	 * @author tanyaowu
	 * 2017年1月2日 下午5:53:06
	 *
	 */
	public TioServer(ServerTioConfig serverTioConfig) {
		super();
		this.serverTioConfig = serverTioConfig;
	}

	/**
	 * @return the serverTioConfig
	 */
	public ServerTioConfig getServerTioConfig() {
		return serverTioConfig;
	}

	/**
	 * @return the serverNode
	 */
	public Node getServerNode() {
		return serverNode;
	}

	/**
	 * @return the serverSocketChannel
	 */
	public AsynchronousServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}

	/**
	 * @return the isWaitingStop
	 */
	public boolean isWaitingStop() {
		return isWaitingStop;
	}

	/**
	 * @param serverTioConfig the serverTioConfig to set
	 */
	public void setServerTioConfig(ServerTioConfig serverTioConfig) {
		this.serverTioConfig = serverTioConfig;
	}

	/**
	 * @param isWaitingStop the isWaitingStop to set
	 */
	public void setWaitingStop(boolean isWaitingStop) {
		this.isWaitingStop = isWaitingStop;
	}

	public void start(String serverIp, int serverPort) throws IOException {
		long start = System.currentTimeMillis();
		this.serverNode = new Node(serverIp, serverPort);
		channelGroup = AsynchronousChannelGroup.withThreadPool(serverTioConfig.groupExecutor);
		serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);

		serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);

		InetSocketAddress listenAddress = null;

		if (StrUtil.isBlank(serverIp)) {
			listenAddress = new InetSocketAddress(serverPort);
		} else {
			listenAddress = new InetSocketAddress(serverIp, serverPort);
		}

		serverSocketChannel.bind(listenAddress, 0);

		AcceptCompletionHandler acceptCompletionHandler = serverTioConfig.getAcceptCompletionHandler();
		serverSocketChannel.accept(this, acceptCompletionHandler);

		serverTioConfig.startTime = System.currentTimeMillis();

		//下面这段代码有点无聊，写得随意，纯粹是为了打印好看些
		String baseStr = "|----------------------------------------------------------------------------------------|";
		int baseLen = baseStr.length();
		StackTraceElement[] ses = Thread.currentThread().getStackTrace();
		StackTraceElement se = ses[ses.length - 1];
		int xxLen = 18;
		int aaLen = baseLen - 3;
		List<String> infoList = new ArrayList<>();
		infoList.add(StrUtil.fillAfter("t-io site", ' ', xxLen) + "| " + SysConst.TIO_URL_SITE);
		infoList.add(StrUtil.fillAfter("t-io on gitee", ' ', xxLen) + "| " + SysConst.TIO_URL_GITEE);
		infoList.add(StrUtil.fillAfter("t-io on github", ' ', xxLen) + "| " + SysConst.TIO_URL_GITHUB);
		infoList.add(StrUtil.fillAfter("t-io version", ' ', xxLen) + "| " + SysConst.TIO_CORE_VERSION);

		infoList.add(StrUtil.fillAfter("-", '-', aaLen));

		infoList.add(StrUtil.fillAfter("TioConfig name", ' ', xxLen) + "| " + serverTioConfig.getName());
		infoList.add(StrUtil.fillAfter("Started at", ' ', xxLen) + "| " + DateUtil.formatDateTime(new Date()));
		infoList.add(StrUtil.fillAfter("Listen on", ' ', xxLen) + "| " + this.serverNode);
		infoList.add(StrUtil.fillAfter("Main Class", ' ', xxLen) + "| " + se.getClassName());

		try {
			RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
			String runtimeName = runtimeMxBean.getName();
			String pid = runtimeName.split("@")[0];
			long startTime = runtimeMxBean.getStartTime();
			long startCost = System.currentTimeMillis() - startTime;
			infoList.add(StrUtil.fillAfter("Jvm start time", ' ', xxLen) + "| " + startCost + "ms");
			infoList.add(StrUtil.fillAfter("Tio start time", ' ', xxLen) + "| " + (System.currentTimeMillis() - start) + "ms");
			infoList.add(StrUtil.fillAfter("Pid", ' ', xxLen) + "| " + pid);
		} catch (Exception e) {

		}
		//100
		String printStr = SysConst.CRLF + baseStr + SysConst.CRLF;
		//		printStr += "|--" + leftStr + " " + info + " " + rightStr + "--|\r\n";
		for (String string : infoList) {
			printStr += "| " + StrUtil.fillAfter(string, ' ', aaLen) + "|\r\n";
		}
		printStr += baseStr + SysConst.CRLF;
		if (log.isInfoEnabled()) {
			log.info(printStr);
		} else {
			System.out.println(printStr);
		}

		checkLastVersion();
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public boolean stop() {
		isWaitingStop = true;
		boolean ret = true;

		try {
			channelGroup.shutdownNow();
		} catch (Exception e) {
			log.error("channelGroup.shutdownNow()时报错", e);
		}

		try {
			serverSocketChannel.close();
		} catch (Exception e1) {
			log.error("serverSocketChannel.close()时报错", e1);
		}

		try {
			serverTioConfig.groupExecutor.shutdown();
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}
		try {
			serverTioConfig.tioExecutor.shutdown();
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}

		serverTioConfig.setStopped(true);
		try {
			ret = ret && serverTioConfig.groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
			ret = ret && serverTioConfig.tioExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		log.info(this.serverNode + " stopped");
		return ret;
	}

	private void checkLastVersion() {
		if (checkLastVersion) {
			serverTioConfig.groupExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						URL url = new URL(SysConst.CHECK_LASTVERSION_URL_1);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(10 * 1000);
						connection.connect();
						int responseCode = connection.getResponseCode();
						if (responseCode == HttpURLConnection.HTTP_OK) {
							InputStream inputStream = connection.getInputStream();
							String result = IoUtils.streamToString(inputStream);

							connection.disconnect();

							url = new URL(SysConst.CHECK_LASTVERSION_URL_2 + result);
							connection = (HttpURLConnection) url.openConnection();
							connection.setRequestMethod("GET");
							connection.setConnectTimeout(10 * 1000);
							connection.connect();
							responseCode = connection.getResponseCode();
							if (responseCode == HttpURLConnection.HTTP_OK) {
								inputStream = connection.getInputStream();
								result = IoUtils.streamToString(inputStream);

								if (SysConst.TIO_CORE_VERSION.equals(result)) {
									log.info("The version you are using is the latest");
								} else {
									log.info("t-io latest version:{}，your version:{}", result, SysConst.TIO_CORE_VERSION);
									//3.5.0.v20190822-RELEASE
									String myVersionDateStr = SysConst.TIO_CORE_VERSION.substring(SysConst.TIO_CORE_VERSION.length() - 16, SysConst.TIO_CORE_VERSION.length() - 8);
									String latestVersionDateStr = result.substring(result.length() - 16, result.length() - 8);

									SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

									Date myVersionDate = format.parse(myVersionDateStr);
									Date latestVersionDate = format.parse(latestVersionDateStr);
									Integer days = DateUtil.daysBetween(myVersionDate, latestVersionDate);

									log.info("You haven't upgraded in {} days", days);
								}
							}

							connection.disconnect();

						}
					} catch (Exception e) {
//						log.error("", e);
					}
				}
			});
		}
	}

	public boolean isCheckLastVersion() {
		return checkLastVersion;
	}

	public void setCheckLastVersion(boolean checkLastVersion) {
		this.checkLastVersion = checkLastVersion;
	}
}
