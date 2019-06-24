package org.tio.common.starter.configuration;
/**
 * @author fanpan26
 */
public class TioClusterProperties {

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 是否开启集群 默认为 false
     * */
    private boolean enabled = false;
    /**
     * 群组是否集群（同一个群组是否会分布在不同的机器上），false:不集群，默认不集群
     */
    private boolean	group = false;
    /**
     * 用户是否集群（同一个用户是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean	user = true;
    /**
     * ip是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean	ip = true;
    /**
     * id是否集群（在A机器上的客户端是否可以通过channelId发消息给B机器上的客户端），false:不集群，默认集群<br>
     */
    private boolean	channel	= true;

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public boolean isIp() {
        return ip;
    }

    public void setIp(boolean ip) {
        this.ip = ip;
    }

    public boolean isChannel() {
        return channel;
    }

    public void setChannel(boolean channel) {
        this.channel = channel;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean bll) {
        this.all = bll;
    }

    /**
     * 所有连接是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean	all = true;
}
