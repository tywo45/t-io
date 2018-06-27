package org.tio.core.task;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.core.ChannelAction;
import org.tio.core.ChannelContext;
import org.tio.core.maintain.MaintainUtils;
import org.tio.core.ssl.SslUtils;
import org.tio.utils.SystemTimer;
import org.tio.utils.thread.pool.AbstractSynRunnable;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:39:59
 */
public class CloseRunnable extends AbstractSynRunnable {

	private static Logger log = LoggerFactory.getLogger(CloseRunnable.class);

	private ChannelContext channelContext;
	private Throwable throwable;
	private String remark;
	private boolean isNeedRemove;

	public CloseRunnable(ChannelContext channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
	}

	//	/**
	//	 * @author tanyaowu
	//	 * 2017年3月1日 下午1:52:12
	//	 *
	//	 */
	//	public CloseRunnable(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove) {
	//		this.channelContext = channelContext;
	//		this.isServerChannelContext = channelContext instanceof ServerChannelContext;
	//		
	//		this.throwable = throwable;
	//		if (SslUtils.isSsl(channelContext)) {
	//			if (remark == null) {
	//				this.remark = "isHandshakeCompleted:" + channelContext.sslFacadeContext.isHandshakeCompleted();
	//			} else {
	//				this.remark = remark + "。isHandshakeCompleted:" + channelContext.sslFacadeContext.isHandshakeCompleted();
	//			}
	//		} else {
	//			this.remark = remark;
	//		}
	//		
	//		
	//		this.isNeedRemove = isNeedRemove;
	//	}

	/**
	 * @see java.lang.Runnable#run()
	 *
	 * @author tanyaowu
	 * 2017年3月1日 下午1:54:34
	 *
	 */
	@Override
	public void runTask() {
		//		long start = System.currentTimeMillis();
		try {
			try {
				if (channelContext.asynchronousSocketChannel != null) {
					channelContext.asynchronousSocketChannel.close();
				}
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}

			boolean isRemove = this.isNeedRemove;
			if (!isRemove) {
				if (channelContext.isServer()) {
					isRemove = true;
				} else {
					ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;
					if (!ReconnConf.isNeedReconn(clientChannelContext, false)) {
						isRemove = true;
					}
				}
			}

			WriteLock writeLock = channelContext.closeLock.writeLock();
			boolean isLock = writeLock.tryLock();
			if (!isLock) {
				if (isRemove) {
					if (channelContext.isRemoved()) {
						return;
					} else {
						writeLock.lock();
						isLock = true;
					}
				} else {
					return;
				}
			}
			
			try {
				channelContext.stat.timeClosed = SystemTimer.currentTimeMillis();
				channelContext.groupContext.getAioListener().onBeforeClose(channelContext, throwable, remark, isRemove);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}
			
			try {
				channelContext.traceClient(ChannelAction.UNCONNECT, null, null);

				if (channelContext.isClosed() && !isRemove) {
					log.info("{}, {}已经关闭，备注:{}，异常:{}", channelContext.groupContext, channelContext, remark, throwable == null ? "无" : throwable.toString());
					return;
				}

				if (channelContext.isRemoved()) {
					log.info("{}, {}已经删除，备注:{}，异常:{}", channelContext.groupContext, channelContext, remark, throwable == null ? "无" : throwable.toString());
					return;
				}

				//必须先取消任务再清空队列
				channelContext.handlerRunnable.setCanceled(true);
				channelContext.sendRunnable.setCanceled(true);

				channelContext.decodeRunnable.clearMsgQueue();
				channelContext.handlerRunnable.clearMsgQueue();
				channelContext.sendRunnable.clearMsgQueue();

				log.info("{}, {} 准备关闭连接, isNeedRemove:{}, {}", channelContext.groupContext, channelContext, isRemove, remark);

				try {
					if (isRemove) {
						MaintainUtils.remove(channelContext);
					} else {
						ClientGroupContext clientGroupContext = (ClientGroupContext) channelContext.groupContext;
						clientGroupContext.closeds.add(channelContext);
						clientGroupContext.connecteds.remove(channelContext);
						MaintainUtils.close(channelContext);
					}

					channelContext.setRemoved(isRemove);
					channelContext.groupContext.groupStat.closed.incrementAndGet();
					channelContext.stat.setTimeClosed(SystemTimer.currentTimeMillis());
					channelContext.setClosed(true);

					//					try {
					//						aioListener.onAfterClose(channelContext, throwable, remark, isRemove);

					//					} catch (Throwable e) {
					//						log.error(e.toString(), e);
					//					}	

				} catch (Throwable e) {
					log.error(e.toString(), e);
				} finally {
					if (!isRemove && channelContext.isClosed() && !channelContext.isServer()) //不删除且没有连接上，则加到重连队列中
					{
						ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;
						ReconnConf.put(clientChannelContext);
					}
				}
			} catch (Throwable e) {
				log.error(throwable.toString(), e);
			} finally {
				writeLock.unlock();
			}
		} finally {
			channelContext.setWaitingClose(false);
			//			long end = System.currentTimeMillis();
			//			long iv = end - start;
			//			System.out.println(iv);
		}
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * @param throwable the throwable to set
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		if (SslUtils.isSsl(channelContext)) {
			if (remark == null) {
				this.remark = "isHandshakeCompleted:" + channelContext.sslFacadeContext.isHandshakeCompleted();
			} else {
				this.remark = remark + "。isHandshakeCompleted:" + channelContext.sslFacadeContext.isHandshakeCompleted();
			}
		} else {
			this.remark = remark;
		}
	}

	/**
	 * @return the isNeedRemove
	 */
	public boolean isNeedRemove() {
		return isNeedRemove;
	}

	/**
	 * @param isNeedRemove the isNeedRemove to set
	 */
	public void setNeedRemove(boolean isNeedRemove) {
		this.isNeedRemove = isNeedRemove;
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public boolean isNeededExecute() {
		return false;
	}

}
