package org.tio.websocket.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioRedisClusterProperties;

import static org.tio.websocket.starter.configuration.TioWebSocketServerRedisClusterProperties.PREFIX;

/**
 * @author fyp
 * @crate 2019/6/24 21:33
 * @project tio-starters
 */
@ConfigurationProperties(PREFIX)
public class TioWebSocketServerRedisClusterProperties extends TioRedisClusterProperties {
    public static final String PREFIX = "tio.websocket.cluster.redis";
}
