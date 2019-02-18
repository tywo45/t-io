package org.tio.cluster;

/**
 * @author tanyaowu
 * 2017年10月10日 下午1:09:16
 */
public class TioClusterConfig {

	public static final String TIO_CLUSTER_TOPIC = "TIOCORE_CLUSTER";

	private TioClusterTopic tioClusterTopic;

	/**
	 * 群组是否集群（同一个群组是否会分布在不同的机器上），false:不集群，默认不集群
	 */
	private boolean	cluster4group		= false;
	/**
	 * 用户是否集群（同一个用户是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean	cluster4user		= true;
	/**
	 * ip是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean	cluster4ip			= true;
	/**
	 * id是否集群（在A机器上的客户端是否可以通过channelId发消息给B机器上的客户端），false:不集群，默认集群<br>
	 */
	private boolean	cluster4channelId	= true;

	/**
	 * bsid是否集群（在A机器上的客户端是否可以通过bsid发消息给B机器上的客户端），false:不集群，默认集群<br>
	 */
	private boolean	cluster4bsId	= true;
	/**
	 * 所有连接是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean	cluster4all		= true;

	/**
	 * 
	 * @param tioClusterTopic
	 */
	public TioClusterConfig(TioClusterTopic tioClusterTopic) {
		this.tioClusterTopic = tioClusterTopic;
	}

	public void publish(TioClusterVo tioClusterVo) {
		tioClusterTopic.publish(tioClusterVo);
	}

	public boolean isCluster4group() {
		return cluster4group;
	}

	public void setCluster4group(boolean cluster4group) {
		this.cluster4group = cluster4group;
	}

	public boolean isCluster4user() {
		return cluster4user;
	}

	public void setCluster4user(boolean cluster4user) {
		this.cluster4user = cluster4user;
	}

	public boolean isCluster4ip() {
		return cluster4ip;
	}

	public void setCluster4ip(boolean cluster4ip) {
		this.cluster4ip = cluster4ip;
	}

	public boolean isCluster4all() {
		return cluster4all;
	}

	public void setCluster4all(boolean cluster4all) {
		this.cluster4all = cluster4all;
	}

	public boolean isCluster4channelId() {
		return cluster4channelId;
	}

	public void setCluster4channelId(boolean cluster4channelId) {
		this.cluster4channelId = cluster4channelId;
	}

	public boolean isCluster4bsId() {
		return cluster4bsId;
	}

	public void setCluster4bsId(boolean cluster4bsId) {
		this.cluster4bsId = cluster4bsId;
	}

	public TioClusterTopic getTioClusterTopic() {
		return tioClusterTopic;
	}

	public void setTioClusterTopic(TioClusterTopic tioClusterTopic) {
		this.tioClusterTopic = tioClusterTopic;
	}
}
