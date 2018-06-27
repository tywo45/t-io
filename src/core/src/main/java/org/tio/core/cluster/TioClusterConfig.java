package org.tio.core.cluster;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

/**
 * @author tanyaowu
 * 2017年10月10日 下午1:09:16
 */
public class TioClusterConfig {

    public static final String TIO_CLUSTER_TOPIC = "TIOCORE_CLUSTER";

    private String topicSuffix;

    private String topic;

    private RedissonClient redisson;

    public RTopic<TioClusterVo> rtopic;

    /**
     * 群组是否集群（同一个群组是否会分布在不同的机器上），false:不集群，默认不集群
     */
    private boolean cluster4group = false;
    /**
     * 用户是否集群（同一个用户是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean cluster4user = true;
    /**
     * ip是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean cluster4ip = true;
    /**
     * id是否集群（在A机器上的客户端是否可以通过channelId发消息给B机器上的客户端），false:不集群，默认集群<br>
     */
    private boolean cluster4channelId = true;
    /**
     * 所有连接是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean cluster4all = true;


    private TioClusterConfig(String topicSuffix, RedissonClient redisson) {
        this.setTopicSuffix(topicSuffix);
        this.setRedisson(redisson);
    }

    /**
     * tio内置的集群是用redis的topic来实现的，所以不同groupContext就要有一个不同的topicSuffix
     *
     * @param topicSuffix  不同类型的groupContext就要有一个不同的topicSuffix
     * @param redisson
     * @return
     * @author: tanyaowu
     */
    public static TioClusterConfig newInstance(String topicSuffix, RedissonClient redisson) {
        if (redisson == null) {
            throw new IllegalArgumentException(RedissonClient.class.getSimpleName() + "不允许为空");
        }

        TioClusterConfig me = new TioClusterConfig(topicSuffix, redisson);
        me.rtopic = redisson.getTopic(me.topic);
        return me;
    }

    public void publishAsyn(TioClusterVo tioClusterVo) {
        rtopic.publishAsync(tioClusterVo);
    }

    public void publish(TioClusterVo tioClusterVo) {
        rtopic.publish(tioClusterVo);
    }

    /**
     * 添加rtopic的消息监听
     * @param listener
     */
    public void addMessageListener(MessageListener<TioClusterVo> listener) {
        this.rtopic.addListener(listener);
    }

    public RedissonClient getRedisson() {
        return redisson;
    }

    public void setRedisson(RedissonClient redisson) {
        this.redisson = redisson;
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

    public String getTopicSuffix() {
        return topicSuffix;
    }

    public void setTopicSuffix(String topicSuffix) {
        this.topicSuffix = topicSuffix;
        this.topic = topicSuffix + TIO_CLUSTER_TOPIC;

    }

    public String getTopic() {
        return topic;
    }

    public boolean isCluster4channelId() {
        return cluster4channelId;
    }

    public void setCluster4channelId(boolean cluster4channelId) {
        this.cluster4channelId = cluster4channelId;
    }
}
