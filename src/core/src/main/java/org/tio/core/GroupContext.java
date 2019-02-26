package org.tio.core;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.cluster.TioClusterConfig;
import org.tio.cluster.TioClusterMessageListener;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.GroupListener;
import org.tio.core.intf.Packet;
import org.tio.core.intf.TioUuid;
import org.tio.core.maintain.BsIds;
import org.tio.core.maintain.ClientNodes;
import org.tio.core.maintain.Groups;
import org.tio.core.maintain.Ids;
import org.tio.core.maintain.IpBlacklist;
import org.tio.core.maintain.IpStats;
import org.tio.core.maintain.Ips;
import org.tio.core.maintain.Tokens;
import org.tio.core.maintain.Users;
import org.tio.core.ssl.SslConfig;
import org.tio.core.stat.DefaultIpStatListener;
import org.tio.core.stat.GroupStat;
import org.tio.core.stat.IpStatListener;
import org.tio.core.task.CloseRunnable;
import org.tio.utils.SystemTimer;
import org.tio.utils.Threads;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.prop.MapWithLockPropSupport;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * 
 * @author tanyaowu 
 * 2016年10月10日 下午5:25:43
 */
public abstract class GroupContext extends MapWithLockPropSupport {
	static Logger			log					= LoggerFactory.getLogger(GroupContext.class);
	/**
	 * 默认的接收数据的buffer size
	 */
	public static final int	READ_BUFFER_SIZE	= Integer.getInteger("tio.default.read.buffer.size", 2048);

	private final static AtomicInteger ID_ATOMIC = new AtomicInteger();

	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	public boolean isShortConnection = false;

	public SslConfig sslConfig = null;

	public boolean debug = false;

	public GroupStat groupStat = null;

	public boolean statOn = true;

	public PacketConverter packetConverter = null;

	/**
	 * 启动时间
	 */
	public long startTime = SystemTimer.currTime;

	/**
	 * 是否用队列发送
	 */
	public boolean useQueueSend = true;

	/**
	 *  是否用队列解码（系统初始化时确定该值，中途不要变更此值，否则在切换的时候可能导致消息丢失）
	 */
	public boolean useQueueDecode = false;

	/**
	 * 心跳超时时间(单位: 毫秒)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数
	 */
	public long heartbeatTimeout = 1000 * 120;

	/**
	 * 解码出现异常时，是否打印异常日志
	 */
	public boolean logWhenDecodeError = false;

	public PacketHandlerMode packetHandlerMode = PacketHandlerMode.SINGLE_THREAD;//.queue;

	/**
	 * 接收数据的buffer size
	 */
	private int readBufferSize = READ_BUFFER_SIZE;

	private GroupListener groupListener = null;

	private TioUuid tioUuid = new DefaultTioUuid();

	public SynThreadPoolExecutor tioExecutor = null;

	public CloseRunnable closeRunnable;

	public ThreadPoolExecutor			groupExecutor	= null;
	public ClientNodes					clientNodes		= new ClientNodes();
	public SetWithLock<ChannelContext>	connections		= new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());
	public Groups						groups			= new Groups();
	public Users						users			= new Users();
	public Tokens						tokens			= new Tokens();
	public Ids							ids				= new Ids();
	public BsIds						bsIds			= new BsIds();
	public Ips							ips				= new Ips();
	public IpStats						ipStats			= null;

	/**
	 * ip黑名单
	 */
	public IpBlacklist ipBlacklist = null;//new IpBlacklist();

	public MapWithLock<Integer, Packet> waitingResps = new MapWithLock<Integer, Packet>(new HashMap<Integer, Packet>());

	public void share(GroupContext groupContext) {
		this.clientNodes = groupContext.clientNodes;
		this.connections = groupContext.connections;
		this.groups = groupContext.groups;
		this.users = groupContext.users;
		this.tokens = groupContext.tokens;
		this.ids = groupContext.ids;
		this.bsIds = groupContext.bsIds;
		this.ipBlacklist = groupContext.ipBlacklist;
		this.ips = groupContext.ips;
	}

	/**
	 * packet编码成bytebuffer时，是否与ChannelContext相关，false: packet编码与ChannelContext无关
	 */
	//	private boolean isEncodeCareWithChannelContext = true;

	protected String id;

	/**
	 * 解码异常多少次就把ip拉黑
	 */
	protected int maxDecodeErrorCountForIp = 10;

	protected String name = "未命名GroupContext";

	private IpStatListener ipStatListener = DefaultIpStatListener.me;

	private boolean isStopped = false;

	/**
	 * 如果此值不为null，就表示要集群
	 */
	private TioClusterConfig tioClusterConfig = null;

	public GroupContext() {
		this(null, null);
	}

	/**
	 * 
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author: tanyaowu
	 */
	public GroupContext(SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		super();
		this.id = ID_ATOMIC.incrementAndGet() + "";

		this.ipStats = new IpStats(this, null);

		this.tioExecutor = tioExecutor;
		if (this.tioExecutor == null) {
			this.tioExecutor = Threads.getTioExecutor();
		}

		this.groupExecutor = groupExecutor;
		if (this.groupExecutor == null) {
			this.groupExecutor = Threads.getGroupExecutor();
		}

		closeRunnable = new CloseRunnable(this.tioExecutor);
	}

	//	/**
	//	 * 
	//	 * @param tioClusterConfig
	//	 * @param tioExecutor
	//	 * @param groupExecutor
	//	 * @author: tanyaowu
	//	 */
	//	public GroupContext(TioClusterConfig tioClusterConfig, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
	//		this(tioExecutor, groupExecutor);
	//		this.setTioClusterConfig(tioClusterConfig);
	//	}

	/**
	 * 获取AioHandler对象
	 * @return
	 * @author: tanyaowu
	 */
	public abstract AioHandler getAioHandler();

	/**
	 * 获取AioListener对象
	 * @return
	 * @author: tanyaowu
	 */
	public abstract AioListener getAioListener();

	/**
	 * 是否是集群
	 * @return true: 是集群
	 * @author: tanyaowu
	 */
	public boolean isCluster() {
		return tioClusterConfig != null;
	}

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	/**
	 * @return the groupListener
	 */
	public GroupListener getGroupListener() {
		return groupListener;
	}

	//	/**
	//	 * 获取GroupStat对象
	//	 * @return
	//	 * @author: tanyaowu
	//	 */
	//	public abstract GroupStat groupStat;

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the tioUuid
	 */
	public TioUuid getTioUuid() {
		return tioUuid;
	}

	/**
	 * @return the syns
	 */
	public MapWithLock<Integer, Packet> getWaitingResps() {
		return waitingResps;
	}

	/**
	 * @return the isEncodeCareWithChannelContext
	 */
	//	public boolean isEncodeCareWithChannelContext() {
	//		return isEncodeCareWithChannelContext;
	//	}

	//	/**
	//	 * @return the isShortConnection
	//	 */
	//	public boolean isShortConnection {
	//		return isShortConnection;
	//	}

	/**
	 * @return the isStop
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 *
	 * @param byteOrder
	 * @author tanyaowu
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * @param isEncodeCareWithChannelContext the isEncodeCareWithChannelContext to set
	 */
	//	public void setEncodeCareWithChannelContext(boolean isEncodeCareWithChannelContext) {
	//		this.isEncodeCareWithChannelContext = isEncodeCareWithChannelContext;
	//	}

	/**
	 * @param groupListener the groupListener to set
	 */
	public void setGroupListener(GroupListener groupListener) {
		this.groupListener = groupListener;
	}

	/**
	 * @param heartbeatTimeout the heartbeatTimeout to set
	 */
	public void setHeartbeatTimeout(long heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param packetHandlerMode the packetHandlerMode to set
	 */
	public void setPacketHandlerMode(PacketHandlerMode packetHandlerMode) {
		this.packetHandlerMode = packetHandlerMode;
	}

	/**
	 * @param readBufferSize the readBufferSize to set
	 */
	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = Math.min(readBufferSize, TcpConst.MAX_DATA_LENGTH);
	}

	/**
	 * @param isShortConnection the isShortConnection to set
	 */
	public void setShortConnection(boolean isShortConnection) {
		this.isShortConnection = isShortConnection;
	}

	/**
	 * @param isStop the isStop to set
	 */
	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	/**
	 * @param tioUuid the tioUuid to set
	 */
	public void setTioUuid(TioUuid tioUuid) {
		this.tioUuid = tioUuid;
	}

	public TioClusterConfig getTioClusterConfig() {
		return tioClusterConfig;
	}

	public void setTioClusterConfig(TioClusterConfig tioClusterConfig) {
		this.tioClusterConfig = tioClusterConfig;
		if (this.tioClusterConfig != null) {
			this.tioClusterConfig.getTioClusterTopic().addMessageListener(new TioClusterMessageListener(this));
			//			this.tioClusterConfig.addMessageListener(new RedissonMessageListener(this));
		}
	}

	public void setSslConfig(SslConfig sslConfig) {
		this.sslConfig = sslConfig;
	}

	public IpStatListener getIpStatListener() {
		return ipStatListener;
	}

	public void setIpStatListener(IpStatListener ipStatListener) {
		this.ipStatListener = ipStatListener;
		//		this.ipStats.setIpStatListener(ipStatListener);
	}

	public GroupStat getGroupStat() {
		return groupStat;
	}

	/**
	 * 是否用队列解码（系统初始化时确定该值，中途不要变更此值，否则在切换的时候可能导致消息丢失
	 * @param useQueueDecode
	 * @author tanyaowu
	 */
	public void setUseQueueDecode(boolean useQueueDecode) {
		this.useQueueDecode = useQueueDecode;
	}

	/**
	 * 是否用队列发送，可以随时切换
	 * @param useQueueSend
	 * @author tanyaowu
	 */
	public void setUseQueueSend(boolean useQueueSend) {
		this.useQueueSend = useQueueSend;
	}

	/**
	 * 是服务器端还是客户端
	 * @return
	 * @author tanyaowu
	 */
	public abstract boolean isServer();

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public boolean isSsl() {
		return sslConfig != null;
	}
}
