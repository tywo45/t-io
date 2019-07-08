package org.tio.websocket.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioClusterProperties;

/**
 * @author fanpan26
 * @author kuangyoubo
 * */
@ConfigurationProperties("tio.websocket.cluster")
public class TioWebSocketServerClusterProperties extends TioClusterProperties {
}
