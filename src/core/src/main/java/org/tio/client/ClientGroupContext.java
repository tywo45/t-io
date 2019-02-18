package org.tio.client;

import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.ssl.SslConfig;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:31:31
 */
public class ClientGroupContext extends GroupContext {
	static Logger log = LoggerFactory.getLogger(ClientGroupContext.class);

	private ClientAioHandler clientAioHandler = null;

	private ClientAioListener clientAioListener = null;

	protected ReconnConf reconnConf;//重连配置

	private ConnectionCompletionHandler connectionCompletionHandler = new ConnectionCompletionHandler();

	public final SetWithLock<ChannelContext>	connecteds	= new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());
	public final SetWithLock<ChannelContext>	closeds		= new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());

	/**
	 * 不重连
	 * @param aioHandler
	 * @param aioListener
	 * @author tanyaowu
	 */
	public ClientGroupContext(ClientAioHandler aioHandler, ClientAioListener aioListener) {
		this(aioHandler, aioListener, null);
	}

	/**
	 * 
	 * @param aioHandler
	 * @param aioListener
	 * @param reconnConf 不用框架自动重连，就传null
	 */
	public ClientGroupContext(ClientAioHandler aioHandler, ClientAioListener aioListener, ReconnConf reconnConf) {
		this(aioHandler, aioListener, reconnConf, null, null);
	}

	/**
	 * 
	 * @param aioHandler
	 * @param aioListener
	 * @param reconnConf 不用框架自动重连，就传null
	 * @param tioExecutor
	 * @param groupExecutor
	 */
	public ClientGroupContext(ClientAioHandler aioHandler, ClientAioListener aioListener, ReconnConf reconnConf, SynThreadPoolExecutor tioExecutor,
	        ThreadPoolExecutor groupExecutor) {
		super(tioExecutor, groupExecutor);
		this.groupStat = new ClientGroupStat();
		this.setClientAioHandler(aioHandler);
		this.setClientAioListener(aioListener);

		this.reconnConf = reconnConf;
	}

	/**
	 * 使用ssl访问
	 * @throws Exception
	 * @author tanyaowu
	 */
	public void useSsl() throws Exception {
		SslConfig sslConfig = SslConfig.forClient();
		setSslConfig(sslConfig);
	}

	/**
	 * @see org.tio.core.GroupContext#getAioHandler()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:33:46
	 *
	 */
	@Override
	public AioHandler getAioHandler() {
		return this.getClientAioHandler();
	}

	/**
	 * @see org.tio.core.GroupContext#getAioListener()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:33:46
	 *
	 */
	@Override
	public AioListener getAioListener() {
		return this.getClientAioListener();
	}

	/**
	 * @return the clientAioHandler
	 */
	public ClientAioHandler getClientAioHandler() {
		return clientAioHandler;
	}

	/**
	 * @return the clientAioListener
	 */
	public ClientAioListener getClientAioListener() {
		return clientAioListener;
	}

	/**
	 * @return the connectionCompletionHandler
	 */
	public ConnectionCompletionHandler getConnectionCompletionHandler() {
		return connectionCompletionHandler;
	}

	/**
	 * @param clientAioHandler the clientAioHandler to set
	 */
	public void setClientAioHandler(ClientAioHandler clientAioHandler) {
		this.clientAioHandler = clientAioHandler;
	}

	/**
	 * @param clientAioListener the clientAioListener to set
	 */
	public void setClientAioListener(ClientAioListener clientAioListener) {
		this.clientAioListener = clientAioListener;
		if (this.clientAioListener == null) {
			this.clientAioListener = new DefaultClientAioListener();
		}
	}

	/**
	 * @param connectionCompletionHandler the connectionCompletionHandler to set
	 */
	public void setConnectionCompletionHandler(ConnectionCompletionHandler connectionCompletionHandler) {
		this.connectionCompletionHandler = connectionCompletionHandler;
	}

	/**
	 * @param reconnConf the reconnConf to set
	 */
	public void setReconnConf(ReconnConf reconnConf) {
		this.reconnConf = reconnConf;
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public ReconnConf getReconnConf() {
		return reconnConf;
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public boolean isServer() {
		return false;
	}

	@Override
	public String toString() {
		return "ClientGroupContext [name=" + name + "]";
	}
}
