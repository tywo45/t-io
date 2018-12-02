/**
 * 
 */
package org.tio.cluster.redisson;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.tio.cluster.TioClusterConfig;
import org.tio.cluster.TioClusterMessageListener;
import org.tio.cluster.TioClusterTopic;
import org.tio.cluster.TioClusterVo;

/**
 * @author tanyaowu
 *
 */
public class RedissonTioClusterTopic implements TioClusterTopic {

	private RedissonClient redisson = null;

	public RTopic rtopic;

	public RedissonTioClusterTopic(String channel, RedissonClient redisson) {
		this.redisson = redisson;
		this.rtopic = redisson.getTopic(TioClusterConfig.TIO_CLUSTER_TOPIC + channel);
	}

	@Override
	public void publish(TioClusterVo tioClusterVo) {
		rtopic.publishAsync(tioClusterVo);
	}

	@Override
	public void addMessageListener(TioClusterMessageListener tioClusterMessageListener) {
		rtopic.addListener(TioClusterVo.class, new RedissonMessageListener(tioClusterMessageListener));
	}

	public RedissonClient getRedisson() {
		return redisson;
	}

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
}
