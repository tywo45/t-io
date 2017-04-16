package org.tio.core;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.tio.core.intf.Packet;
import org.tio.core.task.DecodeRunnable;
import org.tio.core.task.HandlerRunnable;
import org.tio.core.task.SendRunnable;
import org.tio.json.Json;

import com.xiaoleilu.hutool.date.DatePattern;
import com.xiaoleilu.hutool.date.DateUtil;

public abstract class ChannelContext<SessionContext, P extends Packet, R>
{
	private static Logger log = LoggerFactory.getLogger(ChannelContext.class);

	private Logger clientTraceLog = LoggerFactory.getLogger("tio-client-trace-log");

	private boolean isTraceClient = false;

	public static final String UNKNOWN_ADDRESS_IP = "$UNKNOWN";

	public static final AtomicInteger UNKNOWN_ADDRESS_PORT_SEQ = new AtomicInteger();

	//	private java.util.concurrent.Semaphore sendSemaphore = new Semaphore(1);

	private GroupContext<SessionContext, P, R> groupContext = null;

	private DecodeRunnable<SessionContext, P, R> decodeRunnable = null;

	//	private CloseRunnable<SessionContext, P, R> closeRunnable = null;
	//	private HandlerRunnable<SessionContext, P, R> handlerRunnableHighPrior = null;
	private HandlerRunnable<SessionContext, P, R> handlerRunnableNormPrior = null;

	//	private SendRunnable<SessionContext, P, R> sendRunnableHighPrior = null;
	private SendRunnable<SessionContext, P, R> sendRunnableNormPrior = null;
	private ReentrantReadWriteLock closeLock = new ReentrantReadWriteLock();
	private ReadCompletionHandler<SessionContext, P, R> readCompletionHandler = null;//new ReadCompletionHandler<>(this);
	private WriteCompletionHandler<SessionContext, P, R> writeCompletionHandler = null;//new WriteCompletionHandler<>(this);

	private int reconnCount = 0;//连续重连次数，连接成功后，此值会被重置0

	//	private WriteCompletionHandler<SessionContext, P, R> writeCompletionHandler = new WriteCompletionHandler<>();

	private String userid;

	private boolean isWaitingClose = false;

	private boolean isClosed = true;

	private boolean isRemoved = false;

	private ChannelStat stat = new ChannelStat();

	/** The asynchronous socket channel. */
	private AsynchronousSocketChannel asynchronousSocketChannel;

	private SessionContext sessionContext;

	private String id = java.util.UUID.randomUUID().toString();

	private Node clientNode;

	private String clientNodeTraceFilename;

	private Node serverNode;

	/**
	 * 
	 * @param groupContext
	 * @param asynchronousSocketChannel
	 * @author: tanyaowu
	 */
	public ChannelContext(GroupContext<SessionContext, P, R> groupContext, AsynchronousSocketChannel asynchronousSocketChannel)
	{
		super();
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
	public String toString()
	{
		return this.getClientNode().toString();
	}

	/**
	 * @return the asynchronousSocketChannel
	 */
	public AsynchronousSocketChannel getAsynchronousSocketChannel()
	{
		return asynchronousSocketChannel;
	}

	/**
	 * @return the ext
	 */
	public SessionContext getSessionContext()
	{
		return sessionContext;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @return the remoteNode
	 */
	public Node getClientNode()
	{
		return clientNode;
	}

	/**
	 * @param asynchronousSocketChannel the asynchronousSocketChannel to set
	 */
	public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel)
	{
		this.asynchronousSocketChannel = asynchronousSocketChannel;

		if (asynchronousSocketChannel != null)
		{
			try
			{
				Node clientNode = createClientNode(asynchronousSocketChannel);
				setClientNode(clientNode);
			} catch (IOException e)
			{
				log.info(e.toString(), e);
				assignAnUnknownClientNode();
			}
		} else
		{
			assignAnUnknownClientNode();
		}
	}

	private void assignAnUnknownClientNode()
	{
		Node clientNode = new Node(UNKNOWN_ADDRESS_IP, UNKNOWN_ADDRESS_PORT_SEQ.incrementAndGet());
		setClientNode(clientNode);
	}

	/**
	 * @param ext the ext to set
	 */
	public void setSessionContext(SessionContext sessionContext)
	{
		this.sessionContext = sessionContext;
	}

	/**
	 * @param remoteNode the remoteNode to set
	 */
	private void setClientNode(Node clientNode)
	{
		if (this.clientNode != null)
		{
			try
			{
				groupContext.getClientNodes().remove(this);
			} catch (Exception e1)
			{
				log.error(e1.toString(), e1);
			}
		}

		this.clientNode = clientNode;

		if (this.clientNode != null && !Objects.equals(UNKNOWN_ADDRESS_IP, this.clientNode.getIp()))
		{
			try
			{
				groupContext.getClientNodes().put(this);
			} catch (Exception e1)
			{
				log.error(e1.toString(), e1);
			}
		}

		clientNodeTraceFilename = StringUtils.replaceAll(clientNode.toString(), ":", "_");
	}

	/**
	 * @return the groupContext
	 */
	public GroupContext<SessionContext, P, R> getGroupContext()
	{
		return groupContext;
	}

	/**
	 * @param groupContext the groupContext to set
	 */
	public void setGroupContext(GroupContext<SessionContext, P, R> groupContext)
	{
		this.groupContext = groupContext;

		if (groupContext != null)
		{
			decodeRunnable = new DecodeRunnable<>(this);
			//			closeRunnable = new CloseRunnable<>(this, null, null, groupContext.getCloseExecutor());

			//			handlerRunnableHighPrior = new HandlerRunnable<>(this, groupContext.getHandlerExecutorHighPrior());
			handlerRunnableNormPrior = new HandlerRunnable<>(this, groupContext.getHandlerExecutorNormPrior());

			//			sendRunnableHighPrior = new SendRunnable<>(this, groupContext.getSendExecutorHighPrior());
			sendRunnableNormPrior = new SendRunnable<>(this, groupContext.getSendExecutorNormPrior());

			groupContext.getConnections().add(this);
		}
	}

	/**
	 * @return the readCompletionHandler
	 */
	public ReadCompletionHandler<SessionContext, P, R> getReadCompletionHandler()
	{
		return readCompletionHandler;
	}

	/**
	 * @param readCompletionHandler the readCompletionHandler to set
	 */
	public void setReadCompletionHandler(ReadCompletionHandler<SessionContext, P, R> readCompletionHandler)
	{
		this.readCompletionHandler = readCompletionHandler;
	}

	/**
	 * @return the decodeRunnable
	 */
	public DecodeRunnable<SessionContext, P, R> getDecodeRunnable()
	{
		return decodeRunnable;
	}

	/**
	 * @param decodeRunnable the decodeRunnable to set
	 */
	public void setDecodeRunnable(DecodeRunnable<SessionContext, P, R> decodeRunnable)
	{
		this.decodeRunnable = decodeRunnable;
	}

	//	/**
	//	 * @return the handlerRunnableHighPrior
	//	 */
	//	public HandlerRunnable<SessionContext, P, R> getHandlerRunnableHighPrior()
	//	{
	//		return handlerRunnableHighPrior;
	//	}

	//	/**
	//	 * @param handlerRunnableHighPrior the handlerRunnableHighPrior to set
	//	 */
	//	public void setHandlerRunnableHighPrior(HandlerRunnable<SessionContext, P, R> handlerRunnableHighPrior)
	//	{
	//		this.handlerRunnableHighPrior = handlerRunnableHighPrior;
	//	}

	/**
	 * @return the handlerRunnableNormPrior
	 */
	public HandlerRunnable<SessionContext, P, R> getHandlerRunnableNormPrior()
	{
		return handlerRunnableNormPrior;
	}

	/**
	 * @param handlerRunnableNormPrior the handlerRunnableNormPrior to set
	 */
	public void setHandlerRunnableNormPrior(HandlerRunnable<SessionContext, P, R> handlerRunnableNormPrior)
	{
		this.handlerRunnableNormPrior = handlerRunnableNormPrior;
	}

	//	/**
	//	 * @return the sendRunnableHighPrior
	//	 */
	//	public SendRunnable<SessionContext, P, R> getSendRunnableHighPrior()
	//	{
	//		return sendRunnableHighPrior;
	//	}
	//
	//	/**
	//	 * @param sendRunnableHighPrior the sendRunnableHighPrior to set
	//	 */
	//	public void setSendRunnableHighPrior(SendRunnable<SessionContext, P, R> sendRunnableHighPrior)
	//	{
	//		this.sendRunnableHighPrior = sendRunnableHighPrior;
	//	}

	/**
	 * @return the sendRunnableNormPrior
	 */
	public SendRunnable<SessionContext, P, R> getSendRunnableNormPrior()
	{
		return sendRunnableNormPrior;
	}

	/**
	 * @param sendRunnableNormPrior the sendRunnableNormPrior to set
	 */
	public void setSendRunnableNormPrior(SendRunnable<SessionContext, P, R> sendRunnableNormPrior)
	{
		this.sendRunnableNormPrior = sendRunnableNormPrior;
	}

	//	/**
	//	 * @return the writeCompletionHandler
	//	 */
	//	public WriteCompletionHandler<SessionContext, P, R> getWriteCompletionHandler()
	//	{
	//		return writeCompletionHandler;
	//	}
	//
	//	/**
	//	 * @param writeCompletionHandler the writeCompletionHandler to set
	//	 */
	//	public void setWriteCompletionHandler(WriteCompletionHandler<SessionContext, P, R> writeCompletionHandler)
	//	{
	//		this.writeCompletionHandler = writeCompletionHandler;
	//	}

	/**
	 * @return the userid
	 */
	public String getUserid()
	{
		return userid;
	}

	/**
	 * @param userid the userid to set
	 * 给框架内部用的，用户请勿调用此方法
	 */
	public void setUserid(String userid)
	{
		this.userid = userid;
	}

	/**
	 * @return the isClosed
	 */
	public boolean isClosed()
	{
		return isClosed;
	}

	/**
	 * @param isClosed the isClosed to set
	 */
	public void setClosed(boolean isClosed)
	{
		this.isClosed = isClosed;
		if (isClosed)
		{
			if (clientNode == null || (!UNKNOWN_ADDRESS_IP.equals(clientNode.getIp())))
			{
				String before = this.toString();
				assignAnUnknownClientNode();
				log.info("关闭前{}, 关闭后{}", before, this);
			}
		}
	}

	//	/**
	//	 * @return the closeRunnable
	//	 */
	//	public CloseRunnable<SessionContext, P, R> getCloseRunnable()
	//	{
	//		return closeRunnable;
	//	}
	//
	//	/**
	//	 * @param closeRunnable the closeRunnable to set
	//	 */
	//	public void setCloseRunnable(CloseRunnable<SessionContext, P, R> closeRunnable)
	//	{
	//		this.closeRunnable = closeRunnable;
	//	}

	/**
	 * @return the stat
	 */
	public ChannelStat getStat()
	{
		return stat;
	}

	/**
	 * @param stat the stat to set
	 */
	public void setStat(ChannelStat stat)
	{
		this.stat = stat;
	}

	//	/**
	//	 * @return the sendSemaphore
	//	 */
	//	public java.util.concurrent.Semaphore getSendSemaphore()
	//	{
	//		return sendSemaphore;
	//	}

	/**
	 * @return the writeCompletionHandler
	 */
	public WriteCompletionHandler<SessionContext, P, R> getWriteCompletionHandler()
	{
		return writeCompletionHandler;
	}

	/**
	 * @param writeCompletionHandler the writeCompletionHandler to set
	 */
	public void setWriteCompletionHandler(WriteCompletionHandler<SessionContext, P, R> writeCompletionHandler)
	{
		this.writeCompletionHandler = writeCompletionHandler;
	}

	/**
	 * @return the reConnCount
	 */
	public int getReconnCount()
	{
		return reconnCount;
	}

	/**
	 * @param reConnCount the reConnCount to set
	 */
	public void setReconnCount(int reconnCount)
	{
		this.reconnCount = reconnCount;
	}

	/**
	 * @return the isRemoved
	 */
	public boolean isRemoved()
	{
		return isRemoved;
	}

	/**
	 * @param isRemoved the isRemoved to set
	 */
	public void setRemoved(boolean isRemoved)
	{
		this.isRemoved = isRemoved;
	}

	/**
	 * @return the serverNode
	 */
	public Node getServerNode()
	{
		return serverNode;
	}

	/**
	 * @param serverNode the serverNode to set
	 */
	public void setServerNode(Node serverNode)
	{
		this.serverNode = serverNode;
	}

	/**
	 * @return the closeLock
	 */
	public ReentrantReadWriteLock getCloseLock()
	{
		return closeLock;
	}

	/**
	 * @return the isWaitingClose
	 */
	public boolean isWaitingClose()
	{
		return isWaitingClose;
	}

	/**
	 * @param isWaitingClose the isWaitingClose to set
	 */
	public void setWaitingClose(boolean isWaitingClose)
	{
		this.isWaitingClose = isWaitingClose;
	}

	/** 
	 * @see java.lang.Object#hashCode()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年3月5日 下午5:27:49
	 * 
	 */
	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * @param obj
	 * @return
	 * @author: tanyaowu
	 * 2017年3月5日 下午5:27:49
	 * 
	 */
	@Override
	public boolean equals(Object obj)
	{
		//		if (this == obj)
		//		{
		//			return true;
		//		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		@SuppressWarnings("unchecked")
		ChannelContext<SessionContext, P, R> other = (ChannelContext<SessionContext, P, R>) obj;
		if (Objects.equals(other.id, this.id))
		{
			return true;
		} else
		{
			return false;
		}

	}

	/**
	 * @return the clientTraceLog
	 */
	public Logger getClientTraceLog()
	{
		return clientTraceLog;
	}

	/**
	 * @param clientTraceLog the clientTraceLog to set
	 */
	public void setClientTraceLog(Logger clientTraceLog)
	{
		this.clientTraceLog = clientTraceLog;
	}

	public void traceClient(ClientAction clientAction, Packet packet)
	{
		if (isTraceClient)
		{
			Map<String, Object> map = new HashMap<>();
			map.put("time", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN));
			map.put("action", clientAction);
			map.put("c_id", this.id);
			
			
			MDC.put("tio_client", clientNodeTraceFilename);
			
			if (packet != null)
			{
				map.put("p_id", packet.getId());
				map.put("p_respId", packet.getRespId());
				
				map.put("packet", packet.logstr());
			}
			clientTraceLog.info(Json.toJson(map));
		}
	}

	/**
	 * @return the isTraceClient
	 */
	public boolean isTraceClient()
	{
		return isTraceClient;
	}

	/**
	 * @param isTraceClient the isTraceClient to set
	 */
	public void setTraceClient(boolean isTraceClient)
	{
		this.isTraceClient = isTraceClient;
	}

	/**
	 * @return the clientNodeTraceFilename
	 */
	public String getClientNodeTraceFilename()
	{
		return clientNodeTraceFilename;
	}

	/**
	 * @param clientNodeTraceFilename the clientNodeTraceFilename to set
	 */
	public void setClientNodeTraceFilename(String clientNodeTraceFilename)
	{
		this.clientNodeTraceFilename = clientNodeTraceFilename;
	}

}
