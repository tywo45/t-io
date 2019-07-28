package org.tio.common.starter.configuration;

import org.tio.utils.time.Time;

/**
 * @author fanpan26
 */
public class TioProperties {
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
    /**
     * 添加监控时段，不要添加过多的时间段，因为每个时间段都要消耗一份内存，一般加一个时间段就可以了
     */
    private Long[] ipStatDurations = {Time.MINUTE_1};

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

    public Long[] getIpStatDurations() {
        return ipStatDurations;
    }

    public void setIpStatDurations(Long[] ipStatDurations) {
        this.ipStatDurations = ipStatDurations;
    }
}
