package org.tio.core;

import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ReconnConf;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.ClientTraceHandler;
import org.tio.core.intf.Packet;
import org.tio.core.maintain.ChannelContextMapWithLock;
import org.tio.core.maintain.ChannelContextSetWithLock;
import org.tio.core.maintain.ClientNodes;
import org.tio.core.maintain.Groups;
import org.tio.core.maintain.Users;
import org.tio.core.stat.GroupStat;
import org.tio.core.threadpool.DefaultThreadFactory;
import org.tio.core.threadpool.SynThreadPoolExecutor;
import org.tio.core.threadpool.intf.SynRunnableIntf;

public abstract class GroupContext<SessionContext, P extends Packet, R>
{
	static Logger log = LoggerFactory.getLogger(GroupContext.class);

	public static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 1;

	/**
	 * 默认的心跳超时时间(单位: 毫秒)
	 */
	public static final long DEFAULT_HEARTBEAT_TIMEOUT = 1000 * 120;

	/** 
	 * 默认的接收数据的buffer size
	 */
	public static final int READ_BUFFER_SIZE = Integer.getInteger("tio.default.read.buffer.size", 2048);

	public static final long KEEP_ALIVE_TIME = 9000000L;

	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	/**
	 * 心跳超时时间(单位: 毫秒)
	 */
	protected long heartbeatTimeout = DEFAULT_HEARTBEAT_TIMEOUT;
	
	

	private PacketHandlerMode packetHandlerMode = PacketHandlerMode.SINGLE_THREAD;//.queue;
	
	private PacketSendMode packetSendMode = PacketSendMode.QUEUE;

	/**
	 * 接收数据的buffer size
	 */
	protected int readBufferSize = READ_BUFFER_SIZE;

	protected ReconnConf<SessionContext, P, R> reconnConf;//重连配置

	/**
	 * 低优先级的业务处理线程池
	 */
	private SynThreadPoolExecutor<SynRunnableIntf> handlerExecutorNormPrior = null;
	
	
	private  ClientTraceHandler<SessionContext, P, R> clientTraceHandler = new DefaultClientTraceHandler<SessionContext, P, R>();

	/**
	 * 低优先级的消息发送线程池
	 */
	private SynThreadPoolExecutor<SynRunnableIntf> sendExecutorNormPrior = null;
	
	/** The group executor. */
	protected ExecutorService groupExecutor = null;

	private ThreadPoolExecutor closePoolExecutor = null;

	protected ClientNodes<SessionContext, P, R> clientNodes = new ClientNodes<>();
	protected ChannelContextSetWithLock<SessionContext, P, R> connections = new ChannelContextSetWithLock<>();
	protected ChannelContextSetWithLock<SessionContext, P, R> connecteds = new ChannelContextSetWithLock<>();
	protected ChannelContextSetWithLock<SessionContext, P, R> closeds = new ChannelContextSetWithLock<>();

	protected Groups<SessionContext, P, R> groups = new Groups<>();
	protected Users<SessionContext, P, R> users = new Users<>();
	protected ChannelContextMapWithLock<SessionContext, P, R> syns = new ChannelContextMapWithLock<>();

	/**
	 * packet编码成bytebuffer时，是否与ChannelContext相关，false: packet编码与ChannelContext无关
	 */
	private boolean isEncodeCareWithChannelContext = true;

	protected String id;

	private boolean isStopped = false;

	private final static AtomicInteger ID_ATOMIC = new AtomicInteger();

	public GroupContext()
	{
		super();
		this.id = ID_ATOMIC.incrementAndGet() + "";

		//		LinkedBlockingQueue<Runnable> poolQueueHighPrior = new LinkedBlockingQueue<Runnable>();
		//		SynThreadPoolExecutor<SynRunnableIntf> executorHighPrior = new SynThreadPoolExecutor<SynRunnableIntf>(CORE_POOL_SIZE, CORE_POOL_SIZE, KEEP_ALIVE_TIME, poolQueueHighPrior,
		//				DefaultThreadFactory.getInstance("t-aio-high-prior", Thread.MAX_PRIORITY), "t-aio-high-prior");
		//		executorHighPrior.prestartAllCoreThreads();

		LinkedBlockingQueue<Runnable> poolQueueNormPrior = new LinkedBlockingQueue<Runnable>();
		SynThreadPoolExecutor<SynRunnableIntf> executorNormPrior = new SynThreadPoolExecutor<SynRunnableIntf>(CORE_POOL_SIZE, CORE_POOL_SIZE, KEEP_ALIVE_TIME, poolQueueNormPrior,
				DefaultThreadFactory.getInstance("t-aio-norm-prior", Thread.NORM_PRIORITY), "t-aio-norm-prior");
		executorNormPrior.prestartAllCoreThreads();

		//		decodeExecutor = executorNormPrior;
		//		closeExecutor = executorNormPrior;//executorHighPrior;
		//		handlerExecutorHighPrior = executorNormPrior;//executorHighPrior;
		handlerExecutorNormPrior = executorNormPrior;
		//		sendExecutorHighPrior = executorNormPrior;//executorHighPrior;
		sendExecutorNormPrior = executorNormPrior;

		LinkedBlockingQueue<Runnable> closeQueue = new LinkedBlockingQueue<Runnable>();
		closePoolExecutor = new ThreadPoolExecutor(0, CORE_POOL_SIZE, 9, TimeUnit.SECONDS, closeQueue, DefaultThreadFactory.getInstance("t-aio-close", Thread.NORM_PRIORITY));

	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor<SynRunnableIntf> getHandlerExecutorNormPrior()
	{
		return handlerExecutorNormPrior;
	}

	/**
	 * 
	 * @param handlerExecutorNormPrior
	 * @author: tanyaowu
	 */
	public void setHandlerExecutorNormPrior(SynThreadPoolExecutor<SynRunnableIntf> handlerExecutorNormPrior)
	{
		this.handlerExecutorNormPrior = handlerExecutorNormPrior;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor<SynRunnableIntf> getSendExecutorNormPrior()
	{
		return sendExecutorNormPrior;
	}

	/**
	 * 
	 * @param sendExecutorNormPrior
	 * @author: tanyaowu
	 */
	public void setSendExecutorNormPrior(SynThreadPoolExecutor<SynRunnableIntf> sendExecutorNormPrior)
	{
		this.sendExecutorNormPrior = sendExecutorNormPrior;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public ByteOrder getByteOrder()
	{
		return byteOrder;
	}

	/**
	 * 
	 * @param byteOrder
	 * @author: tanyaowu
	 */
	public void setByteOrder(ByteOrder byteOrder)
	{
		this.byteOrder = byteOrder;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public ClientNodes<SessionContext, P, R> getClientNodes()
	{
		return clientNodes;
	}

	/**
	 * 
	 * @param clientNodes
	 * @author: tanyaowu
	 */
	public void setClientNodes(ClientNodes<SessionContext, P, R> clientNodes)
	{
		this.clientNodes = clientNodes;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public Groups<SessionContext, P, R> getGroups()
	{
		return groups;
	}

	/**
	 * 
	 * @param groups
	 * @author: tanyaowu
	 */
	public void setGroups(Groups<SessionContext, P, R> groups)
	{
		this.groups = groups;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public Users<SessionContext, P, R> getUsers()
	{
		return users;
	}

	/**
	 * 
	 * @param users
	 * @author: tanyaowu
	 */
	public void setUsers(Users<SessionContext, P, R> users)
	{
		this.users = users;
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the heartbeatTimeout
	 */
	public long getHeartbeatTimeout()
	{
		return heartbeatTimeout;
	}

	/**
	 * @param heartbeatTimeout the heartbeatTimeout to set
	 */
	public void setHeartbeatTimeout(long heartbeatTimeout)
	{
		this.heartbeatTimeout = heartbeatTimeout;
	}

	/**
	 * @return the connections
	 */
	public ChannelContextSetWithLock<SessionContext, P, R> getConnections()
	{
		return connections;
	}

	/**
	 * @param connections the connections to set
	 */
	public void setConnections(ChannelContextSetWithLock<SessionContext, P, R> connections)
	{
		this.connections = connections;
	}

	/**
	 * @return the readBufferSize
	 */
	public int getReadBufferSize()
	{
		return readBufferSize;
	}

	/**
	 * @param readBufferSize the readBufferSize to set
	 */
	public void setReadBufferSize(int readBufferSize)
	{
		this.readBufferSize = readBufferSize;
	}

	/**
	 * @return
	 *
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:32:02
	 * 
	 */
	public abstract AioHandler<SessionContext, P, R> getAioHandler();

	/**
	 * @return
	 *
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:33:02
	 * 
	 */
	public abstract GroupStat getGroupStat();

	/**
	 * @return
	 *
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:33:28
	 * 
	 */
	public abstract AioListener<SessionContext, P, R> getAioListener();

	/**
	 * @return the reconnConf
	 */
	public ReconnConf<SessionContext, P, R> getReconnConf()
	{
		return reconnConf;
	}

	/**
	 * @return the syns
	 */
	public ChannelContextMapWithLock<SessionContext, P, R> getSyns()
	{
		return syns;
	}

	/**
	 * @param syns the syns to set
	 */
	public void setSyns(ChannelContextMapWithLock<SessionContext, P, R> syns)
	{
		this.syns = syns;
	}

	/**
	 * @return the connecteds
	 */
	public ChannelContextSetWithLock<SessionContext, P, R> getConnecteds()
	{
		return connecteds;
	}

	/**
	 * @param connecteds the connecteds to set
	 */
	public void setConnecteds(ChannelContextSetWithLock<SessionContext, P, R> connecteds)
	{
		this.connecteds = connecteds;
	}

	/**
	 * @return the closeds
	 */
	public ChannelContextSetWithLock<SessionContext, P, R> getCloseds()
	{
		return closeds;
	}

	/**
	 * @param closeds the closeds to set
	 */
	public void setCloseds(ChannelContextSetWithLock<SessionContext, P, R> closeds)
	{
		this.closeds = closeds;
	}

	/**
	 * @return the isEncodeCareWithChannelContext
	 */
	public boolean isEncodeCareWithChannelContext()
	{
		return isEncodeCareWithChannelContext;
	}

	/**
	 * @param isEncodeCareWithChannelContext the isEncodeCareWithChannelContext to set
	 */
	public void setEncodeCareWithChannelContext(boolean isEncodeCareWithChannelContext)
	{
		this.isEncodeCareWithChannelContext = isEncodeCareWithChannelContext;
	}

	/**
	 * @return the isStop
	 */
	public boolean isStopped()
	{
		return isStopped;
	}

	/**
	 * @param isStop the isStop to set
	 */
	public void setStopped(boolean isStopped)
	{
		this.isStopped = isStopped;
	}

	/**
	 * @return the closePoolExecutor
	 */
	public ThreadPoolExecutor getClosePoolExecutor()
	{
		return closePoolExecutor;
	}

	/**
	 * @param closePoolExecutor the closePoolExecutor to set
	 */
	public void setClosePoolExecutor(ThreadPoolExecutor closePoolExecutor)
	{
		this.closePoolExecutor = closePoolExecutor;
	}

	/**
	 * @return the packetHandlerMode
	 */
	public PacketHandlerMode getPacketHandlerMode()
	{
		return packetHandlerMode;
	}

	/**
	 * @param packetHandlerMode the packetHandlerMode to set
	 */
	public void setPacketHandlerMode(PacketHandlerMode packetHandlerMode)
	{
		this.packetHandlerMode = packetHandlerMode;
	}

	/**
	 * @return the packetSendMode
	 */
	public PacketSendMode getPacketSendMode()
	{
		return packetSendMode;
	}

	/**
	 * @param packetSendMode the packetSendMode to set
	 */
	public void setPacketSendMode(PacketSendMode packetSendMode)
	{
		this.packetSendMode = packetSendMode;
	}

	/**
	 * @return the groupExecutor
	 */
	public ExecutorService getGroupExecutor()
	{
		return groupExecutor;
	}

	/**
	 * @param groupExecutor the groupExecutor to set
	 */
	public void setGroupExecutor(ExecutorService groupExecutor)
	{
		this.groupExecutor = groupExecutor;
	}

	/**
	 * @return the clientTraceHandler
	 */
	public ClientTraceHandler<SessionContext, P, R> getClientTraceHandler()
	{
		return clientTraceHandler;
	}

	/**
	 * @param clientTraceHandler the clientTraceHandler to set
	 */
	public void setClientTraceHandler(ClientTraceHandler<SessionContext, P, R> clientTraceHandler)
	{
		this.clientTraceHandler = clientTraceHandler;
	}
}
