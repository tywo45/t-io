package org.tio.cluster.redisson;

import org.redisson.api.listener.MessageListener;
import org.tio.cluster.TioClusterMessageListener;
import org.tio.cluster.TioClusterVo;

/**
 * 
 * @author tanyaowu
 */
public class RedissonMessageListener implements MessageListener<TioClusterVo> {

	private TioClusterMessageListener tioClusterMessageListener;

	public RedissonMessageListener(TioClusterMessageListener tioClusterMessageListener) {
		this.tioClusterMessageListener = tioClusterMessageListener;
	}

	@Override
	public void onMessage(CharSequence channel, TioClusterVo tioClusterVo) {
		this.tioClusterMessageListener.onMessage(channel, tioClusterVo);
	}

	public TioClusterMessageListener getTioClusterMessageListener() {
		return tioClusterMessageListener;
	}

	public void setTioClusterMessageListener(TioClusterMessageListener tioClusterMessageListener) {
		this.tioClusterMessageListener = tioClusterMessageListener;
	}
}
