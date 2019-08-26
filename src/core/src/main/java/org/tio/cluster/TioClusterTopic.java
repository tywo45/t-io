/**
 * 
 */
package org.tio.cluster;

/**
 * @author tanyaowu
 *
 */
public interface TioClusterTopic {

	/**
	 * 
	 * @param tioClusterVo
	 * @author tanyaowu
	 */
	void publish(TioClusterVo tioClusterVo);

	/**
	 * 保证你的MessageListener可以调用tioClusterMessageListener.onMessage(String, TioClusterVo)
	 * @param tioClusterMessageListener
	 * @author tanyaowu
	 */
	void addMessageListener(TioClusterMessageListener tioClusterMessageListener);
}
