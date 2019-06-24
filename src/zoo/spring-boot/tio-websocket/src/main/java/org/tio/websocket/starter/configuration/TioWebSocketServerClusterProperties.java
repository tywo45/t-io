package org.tio.websocket.starter.configuration;

import static org.tio.websocket.starter.configuration.TioWebSocketServerClusterProperties.PREFIX;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioClusterProperties;

/**
 * @author fanpan26
 * @author kuangyoubo
 * */
@ConfigurationProperties(PREFIX)
public class TioWebSocketServerClusterProperties extends TioClusterProperties {
    public static final String PREFIX = "tio.websocket.cluster";
}
