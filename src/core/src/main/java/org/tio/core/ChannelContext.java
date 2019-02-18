package org.tio.core;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
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
	private static Logger log = LoggerFactory.getLogger(ChannelContext.class);

	private static final String DEFAULT_ATTUBITE_KEY = "t-io-d-a-k";

	public static final String UNKNOWN_ADDRESS_IP = "$UNKNOWN";

	public static final AtomicInteger UNKNOWN_ADDRESS_PORT_SEQ = new AtomicInteger();

	public boolean isReconnect = false;

	/**
	 * 解码出现异常时，是否打印异常日志
	 * 此值默认与org.tio.core.GroupContext.logWhenDecodeError保持一致
	 */
	public boolean logWhenDecodeError = false;

	/**
	 * 此值不设时，心跳时间取org.tio.core.GroupContext.heartbeatTimeout
	 * 当然这个值如果小于org.tio.core.GroupContext.heartbeatTimeout，定时检查的时间间隔还是以org.tio.core.GroupContext.heartbeatTimeout为准，只是在判断时用此值
	 */
	public Long heartbeatTimeout = null;

	/**
	 * 一个packet所需要的字节数（用于应用告诉框架，下一次解码所需要的字节长度，省去冗余解码带来的性能损耗）
	 */
	public Integer packetNeededLength = null;

	//	private MapWithLock<String, Object> props = null;//

	public GroupContext					groupContext			= null;
	public DecodeRunnable				decodeRunnable			= null;
	public HandlerRunnable				handlerRunnable			= null;
	public SendRunnable					sendRunnable			= null;
	public final ReentrantReadWriteLock	closeLock				= new ReentrantReadWriteLock();
	private ReadCompletionHandler		readCompletionHandler	= null;							//new ReadCompletionHandler(this);
	public WriteCompletionHandler		writeCompletionHandler	= null;							//new WriteCompletionHandler(this);

	public SslFacadeContext sslFacadeContext;

	public String userid;

	private String token;

	private String bsId;

	public boolean isWaitingClose = false;

	public boolean isClosed = true;

	public boolean isRemoved = false;

	public boolean isVirtual = false;

	public boolean hasTempDir = false;

	public final ChannelStat stat = new ChannelStat();

	/** The asynchronous socket channel. */
	public AsynchronousSocketChannel asynchronousSocketChannel;

	private String id = null;

	private Node clientNode;

	//	private String clientNodeTraceFilename;

	private Node serverNode;

	//	private Logger traceSynPacketLog = LoggerFactory.getLogger("tio-client-trace-syn-log");

	/**
	 * 该连接在哪些组中
	 */
	private SetWithLock<String> groups = null;

	public CloseMeta closeMeta = new CloseMeta();

	/**
	 *
	 * @param groupContext
	 * @param asynchronousSocketChannel
	 * @author tanyaowu
	 */
	public ChannelContext(GroupContext groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
		super();
		init(groupContext, asynchronousSocketChannel);

		if (groupContext.sslConfig != null) {
			try {
				SslFacadeContext sslFacadeContext = new SslFacadeContext(this);
				if (groupContext.isServer()) {
					sslFacadeContext.beginHandshake();
				}
			} catch (Exception e) {
				log.error("在开始SSL握手时发生了异常", e);
				Tio.close(this, "在开始SSL握手时发生了异常" + e.getMessage());
				return;
			}
		}
	}

	/**
	 * 创建一个虚拟ChannelContext，主要用来模拟一些操作，真实场景中用得少
	 * @param groupContext
	 */
	public ChannelContext(GroupContext groupContext) {
		isVirtual = true;
		this.groupContext = groupContext;
		Node clientNode = new Node("127.0.0.1", 26254);
		this.clientNode = clientNode;
		this.id = groupContext.getTioUuid().uuid();
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

	public Object getAttribute() {
		return getAttribute(DEFAULT_ATTUBITE_KEY);
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

	public void init(GroupContext groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
		id = groupContext.getTioUuid().uuid();
		this.setGroupContext(groupContext);
		groupContext.ids.bind(this);
		this.setAsynchronousSocketChannel(asynchronousSocketChannel);
		this.readCompletionHandler = new ReadCompletionHandler(this);
		this.writeCompletionHandler = new WriteCompletionHandler(this);
		this.logWhenDecodeError = groupContext.logWhenDecodeError;
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
				if (groupContext.getAioListener() != null) {
					try {
						groupContext.getAioListener().onAfterSent(this, packet, isSentSuccess);
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}

				if (groupContext.statOn) {
					groupContext.groupStat.sentPackets.incrementAndGet();
					stat.sentPackets.incrementAndGet();
				}

				if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
					try {
						for (Long v : groupContext.ipStats.durationList) {
							IpStat ipStat = groupContext.ipStats.get(v, getClientNode().getIp());
							ipStat.getSentPackets().incrementAndGet();
							groupContext.getIpStatListener().onAfterSent(this, packet, isSentSuccess, ipStat);
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
	 * 设置默认属性
	 * @param value
	 * @author tanyaowu
	 */
	public void setAttribute(Object value) {
		setAttribute(DEFAULT_ATTUBITE_KEY, value);
	}

	/**
	 * @param remoteNode the remoteNode to set
	 */
	public void setClientNode(Node clientNode) {
		if (!this.groupContext.isShortConnection) {
			if (this.clientNode != null) {
				groupContext.clientNodes.remove(this);
			}
		}

		this.clientNode = clientNode;
		if (this.groupContext.isShortConnection) {
			return;
		}

		if (this.clientNode != null && !Objects.equals(UNKNOWN_ADDRESS_IP, this.clientNode.getIp())) {
			groupContext.clientNodes.put(this);
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
	 * @param groupContext the groupContext to set
	 */
	public void setGroupContext(GroupContext groupContext) {
		this.groupContext = groupContext;

		if (groupContext != null) {
			decodeRunnable = new DecodeRunnable(this, groupContext.tioExecutor);
			handlerRunnable = new HandlerRunnable(this, groupContext.tioExecutor);
			sendRunnable = new SendRunnable(this, groupContext.tioExecutor);
			groupContext.connections.add(this);
		}
	}

	public void setGroups(SetWithLock<String> groups) {
		this.groups = groups;
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
	//			this.groupContext.clientTraceHandler.traceChannel(this, channelAction, packet, extmsg);
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

	public GroupContext getGroupContext() {
		return groupContext;
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
}
