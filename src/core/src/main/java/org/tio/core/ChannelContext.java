package org.tio.core;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;
import org.tio.core.intf.PacketWithMeta;
import org.tio.core.task.DecodeRunnable;
import org.tio.core.task.HandlerRunnable;
import org.tio.core.task.SendRunnable;

public abstract class ChannelContext<SessionContext, P extends Packet, R> {
	private static Logger log = LoggerFactory.getLogger(ChannelContext.class);

	public static final String UNKNOWN_ADDRESS_IP = "$UNKNOWN";

	public static final AtomicInteger UNKNOWN_ADDRESS_PORT_SEQ = new AtomicInteger();

	private GroupContext<SessionContext, P, R> groupContext = null;

	private DecodeRunnable<SessionContext, P, R> decodeRunnable = null;

	private HandlerRunnable<SessionContext, P, R> handlerRunnable = null;

	private SendRunnable<SessionContext, P, R> sendRunnable = null;
	private ReentrantReadWriteLock closeLock = new ReentrantReadWriteLock();
	private ReadCompletionHandler<SessionContext, P, R> readCompletionHandler = null;//new ReadCompletionHandler<>(this);
	private WriteCompletionHandler<SessionContext, P, R> writeCompletionHandler = null;//new WriteCompletionHandler<>(this);

	private int reconnCount = 0;//连续重连次数，连接成功后，此值会被重置0

	private String userid;

	private boolean isWaitingClose = false;

	private boolean isClosed = true;

	private boolean isRemoved = false;

	private ChannelStat stat = new ChannelStat();

	/** The asynchronous socket channel. */
	private AsynchronousSocketChannel asynchronousSocketChannel;

	private SessionContext sessionContext;

	private String id = null;

	private Node clientNode;

	private String clientNodeTraceFilename;

	private Node serverNode;

	/**
	 * 
	 * @param groupContext
	 * @param asynchronousSocketChannel
	 * @author: tanyaowu
	 */
	public ChannelContext(GroupContext<SessionContext, P, R> groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
		super();
		id = java.util.UUID.randomUUID().toString();
		groupContext.ids.bind(this);
		this.setGroupContext(groupContext);
		this.setAsynchronousSocketChannel(asynchronousSocketChannel);
		this.readCompletionHandler = new ReadCompletionHandler<>(this);
		this.writeCompletionHandler = new WriteCompletionHandler<>(this);
	}

	/**
	 * 
	 * @param asynchronousSocketChannel
	 * @return
	 * @throws IOException
	 * @author: tanyaowu
	 */
	public abstract Node createClientNode(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException;

	@Override
	public String toString() {
		return this.getClientNode().toString();
	}

	/**
	 * @return the asynchronousSocketChannel
	 */
	public AsynchronousSocketChannel getAsynchronousSocketChannel() {
		return asynchronousSocketChannel;
	}

	/**
	 * @return the ext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the remoteNode
	 */
	public Node getClientNode() {
		return clientNode;
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

	private void assignAnUnknownClientNode() {
		Node clientNode = new Node(UNKNOWN_ADDRESS_IP, UNKNOWN_ADDRESS_PORT_SEQ.incrementAndGet());
		setClientNode(clientNode);
	}

	/**
	 * @param ext the ext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @param remoteNode the remoteNode to set
	 */
	private void setClientNode(Node clientNode) {
		if (this.clientNode != null) {
			try {
				groupContext.clientNodes.remove(this);
			} catch (Exception e1) {
				log.error(e1.toString(), e1);
			}
		}

		this.clientNode = clientNode;

		if (this.clientNode != null && !Objects.equals(UNKNOWN_ADDRESS_IP, this.clientNode.getIp())) {
			try {
				groupContext.clientNodes.put(this);
			} catch (Exception e1) {
				log.error(e1.toString(), e1);
			}
		}

		clientNodeTraceFilename = StringUtils.replaceAll(clientNode.toString(), ":", "_");
	}

	/**
	 * @return the groupContext
	 */
	public GroupContext<SessionContext, P, R> getGroupContext() {
		return groupContext;
	}

	/**
	 * @param groupContext the groupContext to set
	 */
	public void setGroupContext(GroupContext<SessionContext, P, R> groupContext) {
		this.groupContext = groupContext;

		if (groupContext != null) {
			decodeRunnable = new DecodeRunnable<>(this);
			//			closeRunnable = new CloseRunnable<>(this, null, null, groupContext.getCloseExecutor());

			//			handlerRunnableHighPrior = new HandlerRunnable<>(this, groupContext.getHandlerExecutorHighPrior());
			handlerRunnable = new HandlerRunnable<>(this, groupContext.getTioExecutor());

			//			sendRunnableHighPrior = new SendRunnable<>(this, groupContext.getSendExecutorHighPrior());
			sendRunnable = new SendRunnable<>(this, groupContext.getTioExecutor());

			groupContext.connections.add(this);
		}
	}

	/**
	 * @return the readCompletionHandler
	 */
	public ReadCompletionHandler<SessionContext, P, R> getReadCompletionHandler() {
		return readCompletionHandler;
	}

	/**
	 * @return the decodeRunnable
	 */
	public DecodeRunnable<SessionContext, P, R> getDecodeRunnable() {
		return decodeRunnable;
	}

	/**
	 * @return the handlerRunnable
	 */
	public HandlerRunnable<SessionContext, P, R> getHandlerRunnable() {
		return handlerRunnable;
	}

	/**
	 * @return the sendRunnable
	 */
	public SendRunnable<SessionContext, P, R> getSendRunnable() {
		return sendRunnable;
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 * 给框架内部用的，用户请勿调用此方法
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the isClosed
	 */
	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * @param isClosed the isClosed to set
	 */
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
		if (isClosed) {
			if (clientNode == null || (!UNKNOWN_ADDRESS_IP.equals(clientNode.getIp()))) {
				String before = this.toString();
				assignAnUnknownClientNode();
				log.info("关闭前{}, 关闭后{}", before, this);
			}
		}
	}

	/**
	 * @return the stat
	 */
	public ChannelStat getStat() {
		return stat;
	}

	/**
	 * @return the writeCompletionHandler
	 */
	public WriteCompletionHandler<SessionContext, P, R> getWriteCompletionHandler() {
		return writeCompletionHandler;
	}

	/**
	 * @return the reConnCount
	 */
	public int getReconnCount() {
		return reconnCount;
	}

	/**
	 * @param reConnCount the reConnCount to set
	 */
	public void setReconnCount(int reconnCount) {
		this.reconnCount = reconnCount;
	}

	/**
	 * @return the isRemoved
	 */
	public boolean isRemoved() {
		return isRemoved;
	}

	/**
	 * @param isRemoved the isRemoved to set
	 */
	public void setRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
	}

	/**
	 * @return the serverNode
	 */
	public Node getServerNode() {
		return serverNode;
	}

	/**
	 * @param serverNode the serverNode to set
	 */
	public void setServerNode(Node serverNode) {
		this.serverNode = serverNode;
	}

	/**
	 * @return the closeLock
	 */
	public ReentrantReadWriteLock getCloseLock() {
		return closeLock;
	}

	/**
	 * @return the isWaitingClose
	 */
	public boolean isWaitingClose() {
		return isWaitingClose;
	}

	/**
	 * @param isWaitingClose the isWaitingClose to set
	 */
	public void setWaitingClose(boolean isWaitingClose) {
		this.isWaitingClose = isWaitingClose;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	/**
	 * @return the clientNodeTraceFilename
	 */
	public String getClientNodeTraceFilename() {
		return clientNodeTraceFilename;
	}

	/**
	 * @param clientNodeTraceFilename the clientNodeTraceFilename to set
	 */
	public void setClientNodeTraceFilename(String clientNodeTraceFilename) {
		this.clientNodeTraceFilename = clientNodeTraceFilename;
	}

	/**
	 * 
	 * @param obj PacketWithMeta or Packet
	 * @param isSentSuccess
	 * @author: tanyaowu
	 */
	@SuppressWarnings("unchecked")
	public void processAfterSent(Object obj, Boolean isSentSuccess) {
		P packet = null;
		PacketWithMeta<P> packetWithMeta = null;
		boolean isPacket = obj instanceof Packet;
		if (isPacket) {
			packet = (P) obj;
		} else {
			packetWithMeta = (PacketWithMeta<P>) obj;
			packet = packetWithMeta.getPacket();
			CountDownLatch countDownLatch = packetWithMeta.getCountDownLatch();
			countDownLatch.countDown();
		}
		try {
			log.info("{} 已经发送 {}", this, packet.logstr());
			groupContext.getAioListener().onAfterSent(this, packet, isSentSuccess == null ? false : isSentSuccess);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		if (packet.getPacketListener() != null) {
			try {
				packet.getPacketListener().onAfterSent(this, packet, isSentSuccess);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}
}
