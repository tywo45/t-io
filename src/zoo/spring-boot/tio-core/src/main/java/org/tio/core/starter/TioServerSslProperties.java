package org.tio.core.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSL 连接配置
 * @author yangjian
 */
@ConfigurationProperties(prefix = "tio.core.ssl")
public class TioServerSslProperties {

    /**
     * 是否开启 SSL 连接，默认不开启
     */
    private boolean enabled = false;
    /**
     * ssl keystore 文件地址
     */
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
