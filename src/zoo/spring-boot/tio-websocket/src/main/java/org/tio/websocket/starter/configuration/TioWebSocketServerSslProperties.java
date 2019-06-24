package org.tio.websocket.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioSslProperties;

import static org.tio.websocket.starter.configuration.TioWebSocketServerSslProperties.PREFIX;

/**
 * @author fyp
 */
@ConfigurationProperties(PREFIX)
public class TioWebSocketServerSslProperties extends TioSslProperties{
    public static final String PREFIX = "tio.websocket.ssl";
}
