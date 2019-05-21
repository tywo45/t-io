package org.tio.websocket.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.tio.websocket.starter.TioWebSocketServerSslProperties.PREFIX;

/**
 * @author fyp
 */
@ConfigurationProperties(PREFIX)
public class TioWebSocketServerSslProperties {
    public static final String PREFIX = "tio.websocket.ssl";

    private boolean enabled=false;
    private String keyStore;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String trustStore;
    private String password;

    @Override
    public String toString() {
        return "keyStore:" + keyStore + "\n trustStore:" + trustStore;
    }
}
