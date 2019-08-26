package org.tio.websocket.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.common.starter.configuration.TioProperties;
import org.tio.websocket.starter.TioWebSocketClassScanner;

/**
 * @author fanpan26
 * */
@ConfigurationProperties("tio.websocket.server")
public class TioWebSocketServerProperties extends TioProperties {

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
