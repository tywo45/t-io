package org.tio.core.task;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientTioConfig;
import org.tio.client.ReconnConf;
import org.tio.core.ChannelContext;
import org.tio.core.maintain.MaintainUtils;
import org.tio.utils.SystemTimer;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:39:59
 */
public class CloseRunnable extends AbstractQueueRunnable<ChannelContext> {

	private static Logger log = LoggerFactory.getLogger(CloseRunnable.class);

	public CloseRunnable(Executor executor) {
		super(executor);
	}
	//	long count = 1;

	@Override
	public void runTask() {
		int queueSize = msgQueue.size();
		if (queueSize == 0) {
			return;
		}
		ChannelContext channelContext = null;
		while ((channelContext = msgQueue.poll()) != null) {
			//			System.out.println(count++);
			try {
				boolean isNeedRemove = channelContext.closeMeta.isNeedRemove;
				String remark = channelContext.closeMeta.remark;
				Throwable throwable = channelContext.closeMeta.throwable;

				//				WriteLock writeLock = channelContext.closeLock.writeLock();
				//				boolean isLock = writeLock.tryLock();
				//				if (!isLock) {
				//					if (isNeedRemove) {
				//						if (channelContext.isRemoved) {
				//							return;
				//						} else {
				//							writeLock.lock();
				//							isLock = true;
				//						}
				//					} else {
				//						return;
				//					}
				//				}

				channelContext.stat.timeClosed = SystemTimer.currTime;
				if (channelContext.tioConfig.getAioListener() != null) {
					try {
						channelContext.tioConfig.getAioListener().onBeforeClose(channelContext, throwable, remark, isNeedRemove);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}
				}

				try {
					//					channelContext.traceClient(ChannelAction.UNCONNECT, null, null);

					if (channelContext.isClosed && !isNeedRemove) {
						log.info("{}, {}已经关闭，备注:{}，异常:{}", channelContext.tioConfig, channelContext, remark, throwable == null ? "无" : throwable.toString());
						return;
					}

					if (channelContext.isRemoved) {
						log.info("{}, {}已经删除，备注:{}，异常:{}", channelContext.tioConfig, channelContext, remark, throwable == null ? "无" : throwable.toString());
						return;
					}

					//必须先取消任务再清空队列
					channelContext.decodeRunnable.setCanceled(true);
					channelContext.handlerRunnable.setCanceled(true);
					channelContext.sendRunnable.setCanceled(true);
					
					channelContext.decodeRunnable.clearMsgQueue();
					channelContext.handlerRunnable.clearMsgQueue();
					channelContext.sendRunnable.clearMsgQueue();

					log.info("{}, {} 准备关闭连接, isNeedRemove:{}, {}", channelContext.tioConfig, channelContext, isNeedRemove, remark);

					try {
						if (isNeedRemove) {
							MaintainUtils.remove(channelContext);
						} else {
							ClientTioConfig clientTioConfig = (ClientTioConfig) channelContext.tioConfig;
							clientTioConfig.closeds.add(channelContext);
							clientTioConfig.connecteds.remove(channelContext);
							MaintainUtils.close(channelContext);
						}

						channelContext.setRemoved(isNeedRemove);
						if (channelContext.tioConfig.statOn) {
							channelContext.tioConfig.groupStat.closed.incrementAndGet();
						}
						channelContext.stat.timeClosed = SystemTimer.currTime;
						channelContext.setClosed(true);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					} finally {
						if (!isNeedRemove && channelContext.isClosed && !channelContext.isServer()) //不删除且没有连接上，则加到重连队列中
						{
							ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;
							ReconnConf.put(clientChannelContext);
						}
					}
				} catch (Throwable e) {
					log.error(throwable.toString(), e);
				}
				//				finally {
				//					writeLock.unlock();
				//				}
			} finally {
				channelContext.isWaitingClose = false;
			}
		}
	}

	@Override
	public String logstr() {
		return super.logstr();
	}
}
