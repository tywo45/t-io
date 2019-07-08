package org.tio.websocket.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioRedisClusterProperties;

/**
 * @author fyp
 * @crate 2019/6/24 21:33
 * @project tio-starters
 */
@ConfigurationProperties("tio.websocket.cluster.redis")
public class TioWebSocketServerRedisClusterProperties extends TioRedisClusterProperties {
}
