package org.tio.http.client;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.http.common.HttpConst;
import org.tio.utils.Threads;
import org.tio.utils.date.DateUtils;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * 提醒：本工程主要是用于做http暴力测试，代码写得比较了草，新手请勿模仿
 * @author tanyaowu
 */
public class HttpClientStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HttpClientStarter.class);

	private HttpClientAioHandler httpClientAioHandler = new HttpClientAioHandler();

	private HttpClientAioListener httpClientAioListener = new HttpClientAioListener();

	//断链后自动连接的，不想自动连接请设为null
	private ReconnConf reconnConf = null;//new ReconnConf(5000L);

	//一组连接共用的上下文对象
	public ClientGroupContext clientGroupContext = new ClientGroupContext(httpClientAioHandler, httpClientAioListener, reconnConf);

	private TioClient tioClient = null;

	/**
	 * @throws IOException 
	 * 
	 */
	public HttpClientStarter() throws IOException {
		this(null, null);
	}

	/**
	 * 
	 * @param tioExecutor
	 * @param groupExecutor
	 * @throws IOException 
	 */
	public HttpClientStarter(SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
		if (tioExecutor == null) {
			tioExecutor = Threads.getTioExecutor();
		}

		if (groupExecutor == null) {
			groupExecutor = Threads.getGroupExecutor();
		}

		init(tioExecutor, groupExecutor);
	}

	/**
	 * @return the httpClientAioHandler
	 */
	public HttpClientAioHandler getHttpClientAioHandler() {
		return httpClientAioHandler;
	}

	/**
	 * @return the httpClientAioListener
	 */
	public HttpClientAioListener getHttpClientAioListener() {
		return httpClientAioListener;
	}

	/**
	 * @return the clientGroupContext
	 */
	public ClientGroupContext getClientGroupContext() {
		return clientGroupContext;
	}

	private void init(SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
		//		String system_timer_period = System.getProperty("tio.system.timer.period");
		//		if (StrUtil.isBlank(system_timer_period)) {
		//			System.setProperty("tio.system.timer.period", "50");
		//		}
		clientGroupContext.setName("Tio Http Client");
		clientGroupContext.statOn = false;
		tioClient = new TioClient(clientGroupContext);
	}

	public void stop() throws IOException {
		tioClient.stop();
	}

	public static int						requestCount				= 10000;						//每个客户端的请求数
	public static int						clientCount					= 1000;							//客户端个数
	public static int						totalRequestCount			= requestCount * clientCount;	//总请求数
	public static int						stepCount					= totalRequestCount / 10;
	public static ClientChannelContext[]	clientChannelContextArray	= null;
	public static long						startTime					= System.currentTimeMillis();
	public static long						stageStartTime				= System.currentTimeMillis();

	//received 
	public static AtomicLong	receivedCount		= new AtomicLong();
	public static AtomicLong	receivedStageCount	= new AtomicLong();

	public static AtomicLong	receivedBytes		= new AtomicLong();
	public static AtomicLong	receivedStageBytes	= new AtomicLong();

	public static HttpClientStarter	httpClientStarter	= null;
	public static HttpClientStarter	httpsClientStarter	= null;

	public static String requestPath;

	public static void init() throws Exception {
		httpClientStarter = new HttpClientStarter();
		httpsClientStarter = new HttpClientStarter();
		ClientGroupContext clientGroupContext = httpsClientStarter.getClientGroupContext();
		clientGroupContext.useSsl();
	}

	public static void main(String[] args) throws Exception {
		HttpClientStarter httpClientStarter = new HttpClientStarter();

		init();

		httpClientStarter.readCommand();

	}

	/**
	 * 开始测试
	 * @throws Exception
	 * @author tanyaowu
	 */
	public static void startTest(String serverip, int serverport, String path, int clientCount, int requestCount) throws Exception {
		boolean useSsl = false;
		String queryString = "";

		HttpClientStarter httpClientStarter;
		if (useSsl) {
			httpClientStarter = httpsClientStarter;
		} else {
			httpClientStarter = org.tio.http.client.HttpClientStarter.httpClientStarter;
		}

		HttpClientStarter.clientCount = clientCount; //客户端个数
		HttpClientStarter.requestCount = requestCount; //每个客户端的请求数
		HttpClientStarter.requestPath = path;

		HttpClientStarter.totalRequestCount = HttpClientStarter.requestCount * HttpClientStarter.clientCount; //总请求数
		HttpClientStarter.stepCount = HttpClientStarter.totalRequestCount / 10;

		Node serverNode = new Node(serverip, serverport);
		clientChannelContextArray = new ClientChannelContext[clientCount];
		for (int i = 0; i < clientCount; i++) {
			clientChannelContextArray[i] = httpClientStarter.tioClient.connect(serverNode);
		}

		startTime = System.currentTimeMillis();
		stageStartTime = System.currentTimeMillis();

		//received
		receivedCount = new AtomicLong();
		receivedStageCount = new AtomicLong();

		receivedBytes = new AtomicLong();
		receivedStageBytes = new AtomicLong();

		System.out.println("start time:" + startTime + "(" + DateUtils.formatDateTime(new Date(startTime)) + ")");
		ClientHttpRequest clientHttpRequest = ClientHttpRequest.get(requestPath, queryString);
		for (int i = 0; i < clientCount; i++) {
			for (int j = 0; j < requestCount; j++) {
				clientHttpRequest.addHeader(HttpConst.RequestHeaderKey.Host, clientChannelContextArray[i].getServerNode().getIp());
				Tio.send(clientChannelContextArray[i], clientHttpRequest);
			}
		}
	}

	private String helpStr = null;

	String line = "";

	public void readCommand() throws Exception {
		@SuppressWarnings("resource")
		java.util.Scanner sc = new java.util.Scanner(System.in);
		int i = 1;
		StringBuilder sb = new StringBuilder();
		sb.append("使用指南:\r\n");
		sb.append(i++ + "、需要帮助，输入 '?'.\r\n");
		sb.append(i++ + "、get，输入 'get {ip} {port} {requestPath} {clientCount} {requestCount}'.\r\n");
		sb.append("   eg: get 127.0.0.1 8080 /plaintext 100 10000\r\n");

		sb.append(i++ + "、退出程序，输入 'exit'.\r\n");

		helpStr = sb.toString();

		System.out.println(helpStr);

		line = sc.nextLine(); // 这个就是用户输入的数据
		while (true) {
			if ("exit".equalsIgnoreCase(line)) {
				System.out.println("Thanks for using! bye bye.");
				break;
			} else if ("?".equals(line)) {
				System.out.println(sb);
			}

			processCommand(line);

			line = sc.nextLine(); // 这个就是用户输入的数据
		}

		tioClient.stop();
		System.exit(0);
	}

	public void processCommand(String line) throws Exception {
		if (StrUtil.isBlank(line)) {
			return;
		}

		//get {ip} {port} {requestPath} {clientCount} {requestCount}
		String[] args = line.split(" ");
		String command = args[0];
		if ("get".equalsIgnoreCase(command)) {
			if (args.length != 6) {
				System.out.println(helpStr);
				return;
			}
			int i = 1;
			String ip = args[i++];
			int port = Integer.parseInt(args[i++]);
			String requestPath = args[i++];
			int clientCount = Integer.parseInt(args[i++]);
			int requestCount = Integer.parseInt(args[i++]);
			HttpClientStarter.startTest(ip, port, requestPath, clientCount, requestCount);
		} else {

		}
	}

	public static void gsOsc() throws Exception {
		boolean useSsl = false;
		String serverip = "www.baidu.com";
		int serverport = 80;
		String path = "/";
		//		String requestPath = "/json";
		String queryString = "";

		HttpClientStarter httpClientStarter;
		if (useSsl) {
			httpClientStarter = org.tio.http.client.HttpClientStarter.httpsClientStarter;
		} else {
			httpClientStarter = org.tio.http.client.HttpClientStarter.httpClientStarter;
		}

		Node serverNode = new Node(serverip, serverport);
		clientChannelContextArray = new ClientChannelContext[clientCount];
		for (int i = 0; i < clientCount; i++) {
			clientChannelContextArray[i] = httpClientStarter.tioClient.connect(serverNode);
		}

		requestCount = 10000; //每个客户端的请求数
		clientCount = 100; //客户端个数
		totalRequestCount = requestCount * clientCount; //总请求数
		stepCount = totalRequestCount / 10;
		startTime = System.currentTimeMillis();
		stageStartTime = System.currentTimeMillis();

		//received
		receivedCount = new AtomicLong();
		receivedStageCount = new AtomicLong();

		System.out.println("start time:" + startTime + "(" + DateUtils.formatDateTime(new Date(startTime)) + ")");
		ClientHttpRequest clientHttpRequest = ClientHttpRequest.get(path, queryString);
		for (int i = 0; i < clientCount; i++) {
			for (int j = 0; j < requestCount; j++) {
				Tio.send(clientChannelContextArray[i], clientHttpRequest);
			}
		}
	}
}
