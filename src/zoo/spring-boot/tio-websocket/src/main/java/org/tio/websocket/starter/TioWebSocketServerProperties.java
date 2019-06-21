package org.tio.websocket.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.tio.websocket.starter.TioWebSocketServerProperties.PREFIX;

/**
 * @author fanpan26
 * */
@ConfigurationProperties(PREFIX)
public class TioWebSocketServerProperties {
    public static final String PREFIX = "tio.websocket.server";

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(Integer heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    private Integer port = 9876;
    private Integer heartbeatTimeout = 60000;

    public String getBinaryType() {
        return binaryType;
    }

    public void setBinaryType(String binaryType) {
        this.binaryType = binaryType;
    }

    private String binaryType = "text";


}
