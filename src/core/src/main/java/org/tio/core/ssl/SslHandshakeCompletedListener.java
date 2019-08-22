/**
 * 
 */
package org.tio.core.ssl;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.facade.IHandshakeCompletedListener;

/**
 * @author tanyaowu
 *
 */
public class SslHandshakeCompletedListener implements IHandshakeCompletedListener {
	private static Logger log = LoggerFactory.getLogger(SslHandshakeCompletedListener.class);

	private ChannelContext channelContext;

	/**
	 * 
	 */
	public SslHandshakeCompletedListener(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void onComplete() {
		log.info("{}, 完成SSL握手", channelContext);
		channelContext.sslFacadeContext.setHandshakeCompleted(true);

		if (channelContext.tioConfig.getAioListener() != null) {
			try {
				channelContext.tioConfig.getAioListener().onAfterConnected(channelContext, true, channelContext.isReconnect);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}

		ConcurrentLinkedQueue<Packet> forSendAfterSslHandshakeCompleted = channelContext.sendRunnable.getForSendAfterSslHandshakeCompleted(false);
		if (forSendAfterSslHandshakeCompleted == null || forSendAfterSslHandshakeCompleted.size() == 0) {
			return;
		}

		log.info("{} 业务层在SSL握手前就有{}条数据待发送", channelContext, forSendAfterSslHandshakeCompleted.size());
		while (true) {
			Packet packet = forSendAfterSslHandshakeCompleted.poll();
			if (packet != null) {
				if (channelContext.tioConfig.useQueueSend) {
					channelContext.sendRunnable.addMsg(packet);
				} else {
					channelContext.sendRunnable.sendPacket(packet);
				}

			} else {
				break;
			}
		}
		if (channelContext.tioConfig.useQueueSend) {
			channelContext.sendRunnable.execute();
		}
	}

}
