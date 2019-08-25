package org.tio.core;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;
import org.tio.core.intf.Packet.Meta;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.core.stat.ChannelStat;
import org.tio.core.stat.IpStat;
import org.tio.core.task.DecodeRunnable;
import org.tio.core.task.HandlerRunnable;
import org.tio.core.task.SendRunnable;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.prop.MapWithLockPropSupport;

/**
 *
 * @author tanyaowu
 * 2017年10月19日 上午9:39:46
 */
public abstract class ChannelContext extends MapWithLockPropSupport {
	private static Logger				log							= LoggerFactory.getLogger(ChannelContext.class);
	private static final String			DEFAULT_ATTUBITE_KEY		= "t-io-d-a-k";
	public static final String			UNKNOWN_ADDRESS_IP			= "$UNKNOWN";
	public static final AtomicInteger	UNKNOWN_ADDRESS_PORT_SEQ	= new AtomicInteger();
	public boolean						isReconnect					= false;
	/**
	 * 解码出现异常时，是否打印异常日志
	 * 此值默认与org.tio.core.TioConfig.logWhenDecodeError保持一致
	 */
	public boolean						logWhenDecodeError			= false;
	/**
	 * 此值不设时，心跳时间取org.tio.core.TioConfig.heartbeatTimeout
	 * 当然这个值如果小于org.tio.core.TioConfig.heartbeatTimeout，定时检查的时间间隔还是以org.tio.core.TioConfig.heartbeatTimeout为准，只是在判断时用此值
	 */
	public Long							heartbeatTimeout			= null;
	/**
	 * 一个packet所需要的字节数（用于应用告诉框架，下一次解码所需要的字节长度，省去冗余解码带来的性能损耗）
	 */
	public Integer						packetNeededLength			= null;
	public TioConfig					tioConfig					= null;
	public DecodeRunnable				decodeRunnable				= null;
	public HandlerRunnable				handlerRunnable				= null;
	public SendRunnable					sendRunnable				= null;
	public final ReentrantReadWriteLock	closeLock					= new ReentrantReadWriteLock();
	private ReadCompletionHandler		readCompletionHandler		= null;											//new ReadCompletionHandler(this);
	public WriteCompletionHandler		writeCompletionHandler		= null;											//new WriteCompletionHandler(this);
	public SslFacadeContext				sslFacadeContext;
	public String						userid;
	private String						token;
	private String						bsId;
	public boolean						isWaitingClose				= false;
	public boolean						isClosed					= true;
	public boolean						isRemoved					= false;
	public boolean						isVirtual					= false;
	public boolean						hasTempDir					= false;
	public final ChannelStat			stat						= new ChannelStat();
	/** The asynchronous socket channel. */
	public AsynchronousSocketChannel	asynchronousSocketChannel;
	private String						id							= null;
	private Node						clientNode;
	private Node						proxyClientNode				= null;											//一些连接是代理的，譬如web服务器放在nginx后面，此时需要知道最原始的ip
	private Node						serverNode;
	/**
	 * 该连接在哪些组中
	 */
	private SetWithLock<String>			groups						= null;
	private Integer						readBufferSize				= null;											//个性化readBufferSize
	public CloseMeta					closeMeta					= new CloseMeta();
	private CloseCode					closeCode					= CloseCode.INIT_STATUS;						//连接关闭的原因码               

	/**
	 *
	 * @param tioConfig
	 * @param asynchronousSocketChannel
	 * @author tanyaowu
	 */
	public ChannelContext(TioConfig tioConfig, AsynchronousSocketChannel asynchronousSocketChannel) {
		super();
		init(tioConfig, asynchronousSocketChannel);

		if (tioConfig.sslConfig != null) {
			try {
				SslFacadeContext sslFacadeContext = new SslFacadeContext(this);
				if (tioConfig.isServer()) {
					sslFacadeContext.beginHandshake();
				}
			} catch (Exception e) {
				log.error("在开始SSL握手时发生了异常", e);
				Tio.close(this, "在开始SSL握手时发生了异常" + e.getMessage(), CloseCode.SSL_ERROR_ON_HANDSHAKE);
				return;
			}
		}
	}

	/**
	 * 创建一个虚拟ChannelContext，主要用来模拟一些操作，譬如压力测试，真实场景中用得少
	 * @param tioConfig
	 */
	public ChannelContext(TioConfig tioConfig) {
		this(tioConfig, tioConfig.getTioUuid().uuid());
	}

	/**
	 * 创建一个虚拟ChannelContext，主要用来模拟一些操作，譬如压力测试，真实场景中用得少
	 * @param tioConfig
	 * @param id ChannelContext id
	 * @author tanyaowu
	 */
	public ChannelContext(TioConfig tioConfig, String id) {
		isVirtual = true;
		this.tioConfig = tioConfig;
		Node clientNode = new Node("127.0.0.1", 26254);
		this.clientNode = clientNode;
		this.id = id;//tioConfig.getTioUuid().uuid();
		if (StrUtil.isBlank(id)) {
			this.id = tioConfig.getTioUuid().uuid();
		}

		initOther();
	}

	private void assignAnUnknownClientNode() {
		Node clientNode = new Node(UNKNOWN_ADDRESS_IP, UNKNOWN_ADDRESS_PORT_SEQ.incrementAndGet());
		setClientNode(clientNode);
	}

	/**
	 * 创建Node
	 * @param asynchronousSocketChannel
	 * @return
	 * @throws IOException
	 * @author tanyaowu
	 */
	public abstract Node createClientNode(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException;

	/**
	 *
	 * @param obj
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChannelContext other = (ChannelContext) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * 等价于：getAttribute(DEFAULT_ATTUBITE_KEY)
	 * @deprecated 建议使用get()
	 * @return
	 */
	public Object getAttribute() {
		return get();
	}

	/**
	 * 等价于：getAttribute(DEFAULT_ATTUBITE_KEY)<br>
	 * 等价于：getAttribute()<br>
	 * @return
	 */
	public Object get() {
		return get(DEFAULT_ATTUBITE_KEY);
	}

	/**
	 * @return the remoteNode
	 */
	public Node getClientNode() {
		return clientNode;
	}

	public SetWithLock<String> getGroups() {
		return groups;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the readCompletionHandler
	 */
	public ReadCompletionHandler getReadCompletionHandler() {
		return readCompletionHandler;
	}

	/**
	 * @return the serverNode
	 */
	public Node getServerNode() {
		return serverNode;
	}

	public String getToken() {
		return token;
	}

	/**
	 * @return the writeCompletionHandler
	 */
	public WriteCompletionHandler getWriteCompletionHandler() {
		return writeCompletionHandler;
	}

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public int hashCode() {
		if (StrUtil.isNotBlank(id)) {
			return this.id.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public void init(TioConfig tioConfig, AsynchronousSocketChannel asynchronousSocketChannel) {
		id = tioConfig.getTioUuid().uuid();
		this.setTioConfig(tioConfig);
		tioConfig.ids.bind(this);
		this.setAsynchronousSocketChannel(asynchronousSocketChannel);
		this.readCompletionHandler = new ReadCompletionHandler(this);
		this.writeCompletionHandler = new WriteCompletionHandler(this);
		this.logWhenDecodeError = tioConfig.logWhenDecodeError;

		initOther();
	}

	void initOther() {
		if (!tioConfig.isShortConnection) {
			//在长连接中，绑定群组几乎是必须要干的事，所以直接在初始化时给它赋值，省得在后面做同步处理
			groups = new SetWithLock<String>(new HashSet<>());
		}
	}

	/**
	 * 
	 * @param packet
	 * @param isSentSuccess
	 * @author tanyaowu
	 */
	public void processAfterSent(Packet packet, Boolean isSentSuccess) {
		isSentSuccess = isSentSuccess == null ? false : isSentSuccess;
		Meta meta = packet.getMeta();
		if (meta != null) {
			CountDownLatch countDownLatch = meta.getCountDownLatch();
			//			traceBlockPacket(SynPacketAction.BEFORE_DOWN, packet, countDownLatch, null);
			countDownLatch.countDown();
		}

		try {
			if (log.isDebugEnabled()) {
				log.debug("{} 已经发送 {}", this, packet.logstr());
			}

			//非SSL or SSL已经握手
			if (this.sslFacadeContext == null || this.sslFacadeContext.isHandshakeCompleted()) {
				if (tioConfig.getAioListener() != null) {
					try {
						tioConfig.getAioListener().onAfterSent(this, packet, isSentSuccess);
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}

				if (tioConfig.statOn) {
					tioConfig.groupStat.sentPackets.incrementAndGet();
					stat.sentPackets.incrementAndGet();
				}

				if (tioConfig.ipStats.durationList != null && tioConfig.ipStats.durationList.size() > 0) {
					try {
						for (Long v : tioConfig.ipStats.durationList) {
							IpStat ipStat = tioConfig.ipStats.get(v, this);
							ipStat.getSentPackets().incrementAndGet();
							tioConfig.getIpStatListener().onAfterSent(this, packet, isSentSuccess, ipStat);
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}

		if (packet.getPacketListener() != null) {
			try {
				packet.getPacketListener().onAfterSent(this, packet, isSentSuccess);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}
		}

	}

	/**
	 * @param asynchronousSocketChannel the asynchronousSocketChannel to set
	 */
	public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
		this.asynchronousSocketChannel = asynchronousSocketChannel;

		if (asynchronousSocketChannel != null) {
			try {
				Node clientNode = createClientNode(asynchronousSocketChannel);
				setClientNode(clientNode);
			} catch (IOException e) {
				log.info(e.toString(), e);
				assignAnUnknownClientNode();
			}
		} else {
			assignAnUnknownClientNode();
		}
	}

	/**
	 * 等价于：setAttribute(DEFAULT_ATTUBITE_KEY, value)<br>
	 * 仅仅是为了内部方便，不建议大家使用<br>
	 * @deprecated 不建议各位同学使用这个方法，建议使用set("name1", object1)
	 * @param value
	 * @author tanyaowu
	 */
	public void setAttribute(Object value) {
		set(value);
	}

	/**
	 * 等价于：set(DEFAULT_ATTUBITE_KEY, value)<br>
	 * 等价于：setAttribute(Object value)<br>
	 * @deprecated 不建议各位同学使用这个方法，建议使用set("name1", object1)
	 * @param value
	 */
	public void set(Object value) {
		set(DEFAULT_ATTUBITE_KEY, value);
	}

	/**
	 * @param remoteNode the remoteNode to set
	 */
	public void setClientNode(Node clientNode) {
		if (!this.tioConfig.isShortConnection) {
			if (this.clientNode != null) {
				tioConfig.clientNodes.remove(this);
			}
		}

		this.clientNode = clientNode;
		if (this.tioConfig.isShortConnection) {
			return;
		}

		if (this.clientNode != null && !Objects.equals(UNKNOWN_ADDRESS_IP, this.clientNode.getIp())) {
			tioConfig.clientNodes.put(this);
			//			clientNodeTraceFilename = StrUtil.replaceAll(clientNode.toString(), ":", "_");
		}
	}

	/**
	 * @param isClosed the isClosed to set
	 */
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
		if (isClosed) {
			if (clientNode == null || !UNKNOWN_ADDRESS_IP.equals(clientNode.getIp())) {
				String before = this.toString();
				assignAnUnknownClientNode();
				log.info("关闭前{}, 关闭后{}", before, this);
			}
		}
	}

	/**
	 * @param tioConfig the tioConfig to set
	 */
	public void setTioConfig(TioConfig tioConfig) {
		this.tioConfig = tioConfig;

		if (tioConfig != null) {
			decodeRunnable = new DecodeRunnable(this, tioConfig.tioExecutor);
			handlerRunnable = new HandlerRunnable(this, tioConfig.tioExecutor);
			sendRunnable = new SendRunnable(this, tioConfig.tioExecutor);
			tioConfig.connections.add(this);
		}
	}

	public void setPacketNeededLength(Integer packetNeededLength) {
		this.packetNeededLength = packetNeededLength;
	}

	public void setReconnect(boolean isReconnect) {
		this.isReconnect = isReconnect;
	}

	/**
	 * @param isRemoved the isRemoved to set
	 */
	public void setRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
	}

	/**
	 * @param serverNode the serverNode to set
	 */
	public void setServerNode(Node serverNode) {
		this.serverNode = serverNode;
	}

	public void setSslFacadeContext(SslFacadeContext sslFacadeContext) {
		this.sslFacadeContext = sslFacadeContext;
	}

	public void setToken(String token) {
		this.token = token;
	}

	//	/**
	//	 * @param isTraceClient the isTraceClient to set
	//	 */
	//	public void setTraceClient(boolean isTraceClient) {
	//		this.isTraceClient = isTraceClient;
	//	}

	//	/**
	//	 * @param isTraceSynPacket the isTraceSynPacket to set
	//	 */
	//	public void setTraceSynPacket(boolean isTraceSynPacket) {
	//		this.isTraceSynPacket = isTraceSynPacket;
	//	}

	/**
	 * @param userid the userid to set
	 * 给框架内部用的，用户请勿调用此方法
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		if (serverNode != null) {
			sb.append("server:").append(serverNode.toString());
		} else {
			sb.append("server:").append("NULL");
		}
		if (clientNode != null) {
			sb.append(", client:").append(clientNode.toString());
		} else {
			sb.append(", client:").append("NULL");
		}

		if (isVirtual) {
			sb.append(", virtual");
		}

		return sb.toString();
	}

	//	/**
	//	 * 跟踪同步消息，主要是跟踪锁的情况，用于问题排查。
	//	 * @param synPacketAction
	//	 * @param packet
	//	 * @param extmsg
	//	 * @author tanyaowu
	//	 */
	//	public void traceBlockPacket(SynPacketAction synPacketAction, Packet packet, CountDownLatch countDownLatch, Map<String, Object> extmsg) {
	//		if (isTraceSynPacket) {
	//			ChannelContext channelContext = this;
	//			Map<String, Object> map = new HashMap<>(10);
	//			map.put("currTime", DateTime.now().toString(DatePattern.NORM_DATETIME_MS_FORMAT));
	//			map.put("c_id", channelContext.getId());
	//			map.put("c", channelContext.toString());
	//			map.put("action", synPacketAction);
	//
	//			MDC.put("tio_client_syn", channelContext.getClientNodeTraceFilename());
	//
	//			if (packet != null) {
	//				map.put("p_id", channelContext.getClientNode().getPort() + "_" + packet.getId()); //packet id
	//				map.put("p_respId", packet.getRespId());
	//				map.put("packet", packet.logstr());
	//			}
	//
	//			if (countDownLatch != null) {
	//				map.put("countDownLatch", countDownLatch.hashCode() + " " + countDownLatch.getCount());
	//			}
	//
	//			if (extmsg != null) {
	//				map.putAll(extmsg);
	//			}
	//			String logstr = Json.toJson(map);
	//			traceSynPacketLog.info(logstr);
	//			log.error(logstr);
	//
	//		}
	//	}

	//	/**
	//	 * 跟踪消息
	//	 * @param channelAction
	//	 * @param packet
	//	 * @param extmsg
	//	 * @author tanyaowu
	//	 */
	//	public void traceClient(ChannelAction channelAction, Packet packet, Map<String, Object> extmsg) {
	//		if (isTraceClient) {
	//			this.tioConfig.clientTraceHandler.traceChannel(this, channelAction, packet, extmsg);
	//		}
	//	}

	/**
	 * @return the bsId
	 */
	public String getBsId() {
		return bsId;
	}

	/**
	 * @param bsId the bsId to set
	 */
	public void setBsId(String bsId) {
		this.bsId = bsId;
	}

	public TioConfig getTioConfig() {
		return tioConfig;
	}

	/**
	 * 是否是服务器端
	 * @return
	 * @author tanyaowu
	 */
	public abstract boolean isServer();

	/**
	 * @return the heartbeatTimeout
	 */
	public Long getHeartbeatTimeout() {
		return heartbeatTimeout;
	}

	/**
	 * @param heartbeatTimeout the heartbeatTimeout to set
	 */
	public void setHeartbeatTimeout(Long heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public Integer getReadBufferSize() {
		if (readBufferSize != null && readBufferSize > 0) {
			return readBufferSize;
		}
		return this.tioConfig.getReadBufferSize();
	}

	public void setReadBufferSize(Integer readBufferSize) {
		this.readBufferSize = Math.min(readBufferSize, TcpConst.MAX_DATA_LENGTH);
	}

	/**
	 * @return the proxyClientNode
	 */
	public Node getProxyClientNode() {
		return proxyClientNode;
	}

	private void swithIpStat(IpStat oldIpStat, IpStat newIpStat, ChannelStat myStat) {
		oldIpStat.getHandledBytes().addAndGet(-myStat.getHandledBytes().get());
		oldIpStat.getHandledPacketCosts().addAndGet(-myStat.getHandledPacketCosts().get());
		oldIpStat.getHandledPackets().addAndGet(-myStat.getHandledPackets().get());
		oldIpStat.getReceivedBytes().addAndGet(-myStat.getReceivedBytes().get());
		oldIpStat.getReceivedPackets().addAndGet(-myStat.getReceivedPackets().get());
		oldIpStat.getReceivedTcps().addAndGet(-myStat.getReceivedTcps().get());
		oldIpStat.getRequestCount().addAndGet(-1);
		oldIpStat.getSentBytes().addAndGet(-myStat.getSentBytes().get());
		oldIpStat.getSentPackets().addAndGet(-myStat.getSentPackets().get());
		oldIpStat.getStart();

		newIpStat.getHandledBytes().addAndGet(myStat.getHandledBytes().get());
		newIpStat.getHandledPacketCosts().addAndGet(myStat.getHandledPacketCosts().get());
		newIpStat.getHandledPackets().addAndGet(myStat.getHandledPackets().get());
		newIpStat.getReceivedBytes().addAndGet(myStat.getReceivedBytes().get());
		newIpStat.getReceivedPackets().addAndGet(myStat.getReceivedPackets().get());
		newIpStat.getReceivedTcps().addAndGet(myStat.getReceivedTcps().get());
		newIpStat.getRequestCount().addAndGet(1);
		newIpStat.getSentBytes().addAndGet(myStat.getSentBytes().get());
		newIpStat.getSentPackets().addAndGet(myStat.getSentPackets().get());
		newIpStat.getStart();
	}

	/**
	 * @param proxyClientNode the proxyClientNode to set
	 */
	public void setProxyClientNode(Node proxyClientNode) {
		this.proxyClientNode = proxyClientNode;
		if (proxyClientNode != null) {
			//将性能数据进行转移
			if (!Objects.equals(proxyClientNode.getIp(), clientNode.getIp())) {

				if (tioConfig.ipStats.durationList != null && tioConfig.ipStats.durationList.size() > 0) {
					try {
						for (Long v : tioConfig.ipStats.durationList) {
							IpStat oldIpStat = (IpStat) tioConfig.ipStats._get(v, this, true, false);
							IpStat newIpStat = (IpStat) tioConfig.ipStats.get(v, this);
							ChannelStat myStat = this.stat;
							swithIpStat(oldIpStat, newIpStat, myStat);
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
			}
		}
	}

	public CloseCode getCloseCode() {
		return closeCode;
	}

	public void setCloseCode(CloseCode closeCode) {
		this.closeCode = closeCode;
	}

	/**
	 * @author tanyaowu
	 */
	public static class CloseMeta {
		public Throwable	throwable;
		public String		remark;
		public boolean		isNeedRemove;

		public Throwable getThrowable() {
			return throwable;
		}

		public void setThrowable(Throwable throwable) {
			this.throwable = throwable;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public boolean isNeedRemove() {
			return isNeedRemove;
		}

		public void setNeedRemove(boolean isNeedRemove) {
			this.isNeedRemove = isNeedRemove;
		}
	}

	/**
	 * 连接关闭码
	 * @author tanyaowu
	 */
	public static enum CloseCode {
		/**
		 * 没有提供原因码
		 */
		NO_CODE((byte) 1),
		/**
		 * 读异常
		 */
		READ_ERROR((byte) 2),
		/**
		 * 写异常
		 */
		WRITER_ERROR((byte) 3),
		/**
		 * 解码异常
		 */
		DECODE_ERROR((byte) 4),
		/**
		 * 通道未打开
		 */
		CHANNEL_NOT_OPEN((byte) 5),
		/**
		 * 读到的数据长度是0
		 */
		READ_COUNT_IS_ZERO((byte) 6),
		/**
		 * 对方关闭了连接
		 */
		CLOSED_BY_PEER((byte) 7),
		/**
		 * 读到的数据长度小于-1
		 */
		READ_COUNT_IS_NEGATIVE((byte) 8),
		/**
		 * 写数据长度小于0
		 */
		WRITE_COUNT_IS_NEGATIVE((byte) 9),
		/**
		 * 心跳超时
		 */
		HEARTBEAT_TIMEOUT((byte) 10),
		/**
		 * 连接失败
		 */
		CLIENT_CONNECTION_FAIL((byte) 80),

		/**
		 * SSL握手时发生异常
		 */
		SSL_ERROR_ON_HANDSHAKE((byte) 50),
		/**
		 * SSL session关闭了
		 */
		SSL_SESSION_CLOSED((byte) 51),
		/**
		 * SSL加密时发生异常
		 */
		SSL_ENCRYPTION_ERROR((byte) 52),
		/**
		 * SSL解密时发生异常
		 */
		SSL_DECRYPT_ERROR((byte) 53),

		/**
		 * 供用户使用
		 */
		USER_CODE_0((byte) 100),
		/**
		 * 供用户使用
		 */
		USER_CODE_1((byte) 101),
		/**
		 * 供用户使用
		 */
		USER_CODE_2((byte) 102),
		/**
		 * 供用户使用
		 */
		USER_CODE_3((byte) 103),
		/**
		 * 供用户使用
		 */
		USER_CODE_4((byte) 104),
		/**
		 * 供用户使用
		 */
		USER_CODE_5((byte) 105),
		/**
		 * 供用户使用
		 */
		USER_CODE_6((byte) 106),
		/**
		 * 供用户使用
		 */
		USER_CODE_7((byte) 107),
		/**
		 * 供用户使用
		 */
		USER_CODE_8((byte) 108),
		/**
		 * 供用户使用
		 */
		USER_CODE_9((byte) 109),
		/**
		 * 供用户使用
		 */
		USER_CODE_10((byte) 110),
		/**
		 * 初始值
		 */
		INIT_STATUS((byte) 199),
		/**
		 * 其它异常
		 */
		OTHER_ERROR((byte) 200),;

		public static CloseCode from(Byte value) {
			CloseCode[] values = CloseCode.values();
			for (CloseCode v : values) {
				if (Objects.equals(v.value, value)) {
					return v;
				}
			}
			return null;
		}

		Byte value;

		private CloseCode(Byte value) {
			this.value = value;
		}

		public Byte getValue() {
			return value;
		}

		public void setValue(Byte value) {
			this.value = value;
		}
	}
}
