package org.tio.core.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioRedisClusterProperties;

/**
 * @author fyp
 * @crate 2019/6/24 22:28
 * @project t-io
 */
@ConfigurationProperties("tio.core.cluster.redis")
public class TioServerRedisClusterProperties extends TioRedisClusterProperties {
}
