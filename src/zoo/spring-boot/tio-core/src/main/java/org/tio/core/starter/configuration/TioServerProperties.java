package org.tio.core.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioProperties;

/**
 * @author yangjian
 * */
@ConfigurationProperties(prefix = "tio.core.server")
public class TioServerProperties extends TioProperties {

    public TioServerProperties() {
        setPort(6789);
        setHeartbeatTimeout(5000);
    }
}
