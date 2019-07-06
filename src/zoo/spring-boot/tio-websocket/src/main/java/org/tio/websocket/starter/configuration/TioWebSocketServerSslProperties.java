package org.tio.websocket.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioSslProperties;

/**
 * @author fyp
 */
@ConfigurationProperties("tio.websocket.ssl")
public class TioWebSocketServerSslProperties extends TioSslProperties{
}
