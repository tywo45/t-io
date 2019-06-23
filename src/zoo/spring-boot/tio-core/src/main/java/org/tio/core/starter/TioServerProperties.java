package org.tio.core.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yangjian
 * */
@ConfigurationProperties(prefix = "tio.core.server")
public class TioServerProperties {

    /**
     * 服务绑定的 IP 地址，默认不绑定
     */
    private String ip = null;
    /**
     * 服务绑定的端口
     */
    private int port = 6789;
    /**
     * 心跳超时时间，超时会自动关闭连接
     */
    private int heartbeatTimeout = 5000;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
}
