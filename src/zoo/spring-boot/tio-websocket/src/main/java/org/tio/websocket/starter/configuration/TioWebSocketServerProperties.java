package org.tio.websocket.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioProperties;
import org.tio.websocket.starter.TioWebSocketClassScanner;

import static org.tio.websocket.starter.configuration.TioWebSocketServerProperties.PREFIX;

/**
 * @author fanpan26
 * */
@ConfigurationProperties(PREFIX)
public class TioWebSocketServerProperties extends TioProperties {
    public static final String PREFIX = "tio.websocket.server";

    public boolean isUseScanner() {
        return useScanner;
    }

    public void setUseScanner(boolean useScanner) {
        this.useScanner = useScanner;
    }

    /**
     *  use {@link TioWebSocketClassScanner} default false
     * */
    private boolean useScanner = false;

}
