package org.tio.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Node;
import org.tio.utils.SysConst;
import org.tio.utils.date.DateUtils;
import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu
 *
 */
public class TioServer {
	private static Logger log = LoggerFactory.getLogger(TioServer.class);

	private ServerGroupContext serverGroupContext;

	private AsynchronousServerSocketChannel serverSocketChannel;

	private AsynchronousChannelGroup channelGroup = null;

	private Node serverNode;

	private boolean isWaitingStop = false;

	/**
	 *
	 * @param serverGroupContext
	 *
	 * @author tanyaowu
	 * 2017年1月2日 下午5:53:06
	 *
	 */
	public TioServer(ServerGroupContext serverGroupContext) {
		super();
		this.serverGroupContext = serverGroupContext;
	}

	/**
	 * @return the serverGroupContext
	 */
	public ServerGroupContext getServerGroupContext() {
		return serverGroupContext;
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
	 * @param serverGroupContext the serverGroupContext to set
	 */
	public void setServerGroupContext(ServerGroupContext serverGroupContext) {
		this.serverGroupContext = serverGroupContext;
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
		channelGroup = AsynchronousChannelGroup.withThreadPool(serverGroupContext.groupExecutor);
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

		AcceptCompletionHandler acceptCompletionHandler = serverGroupContext.getAcceptCompletionHandler();
		serverSocketChannel.accept(this, acceptCompletionHandler);

		serverGroupContext.startTime = System.currentTimeMillis();

		//下面这段代码有点无聊，写得随意，纯粹是为了打印好看些
		String baseStr = "|----------------------------------------------------------------------------------------|";
		int baseLen = baseStr.length();
		StackTraceElement[] ses = Thread.currentThread().getStackTrace();
		StackTraceElement se = ses[ses.length - 1];
		int xxLen = 18;
		int aaLen = baseLen - 3;
		List<String> infoList = new ArrayList<>();
		infoList.add(StrUtil.fillAfter("Tio on github", ' ', xxLen) + "| " + SysConst.TIO_URL_GITHUB);
		infoList.add(StrUtil.fillAfter("Tio site address", ' ', xxLen) + "| " + SysConst.TIO_URL_SITE);
		infoList.add(StrUtil.fillAfter("Tio version", ' ', xxLen) + "| " + SysConst.TIO_CORE_VERSION);

		infoList.add(StrUtil.fillAfter("-", '-', aaLen));

		infoList.add(StrUtil.fillAfter("GroupContext name", ' ', xxLen) + "| " + serverGroupContext.getName());
		infoList.add(StrUtil.fillAfter("Started at", ' ', xxLen) + "| " + DateUtils.formatDateTime(new Date()));
		infoList.add(StrUtil.fillAfter("Listen on", ' ', xxLen) + "| " + this.serverNode);
		infoList.add(StrUtil.fillAfter("Main Class", ' ', xxLen) + "| " + se.getClassName());

		try {
			RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
			String runtimeName = runtimeMxBean.getName();
			String pid = runtimeName.split("@")[0];
			long startTime = runtimeMxBean.getStartTime();
			long startCost = System.currentTimeMillis() - startTime;
			infoList.add(StrUtil.fillAfter("Jvm start time", ' ', xxLen) + "| " + startCost + " ms");
			infoList.add(StrUtil.fillAfter("Tio start time", ' ', xxLen) + "| " + (System.currentTimeMillis() - start) + " ms");
			infoList.add(StrUtil.fillAfter("Pid", ' ', xxLen) + "| " + pid);

		} catch (Exception e) {

		}
		//100
		String printStr = "\r\n" + baseStr + "\r\n";
		//		printStr += "|--" + leftStr + " " + info + " " + rightStr + "--|\r\n";
		for (String string : infoList) {
			printStr += "| " + StrUtil.fillAfter(string, ' ', aaLen) + "|\r\n";
		}
		printStr += baseStr + "\r\n";
		if (log.isInfoEnabled()) {
			log.info(printStr);
		} else {
			System.out.println(printStr);
		}
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
			serverGroupContext.groupExecutor.shutdown();
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}
		try {
			serverGroupContext.tioExecutor.shutdown();
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}

		serverGroupContext.setStopped(true);
		try {
			ret = ret && serverGroupContext.groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
			ret = ret && serverGroupContext.tioExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		log.info(this.serverNode + " stopped");
		return ret;
	}
}
