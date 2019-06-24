package org.tio.common.starter.configuration;

import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.BeanUtils;

/**
 * @author fanpan26
 */
public class TioRedisClusterProperties {

    private final String SENTINEL = "sentinel";
    private final String CLUSTER = "cluster";

    /**
     * single 单机 cluster 集群 sentinel 哨兵
     * single 已经实现了，就不在这里实现
     */
    private String mode;

    private ClusterServersConfig cluster;

    private SentinelServersConfig sentinel;

    /**
     * 根据beanName直接注入redissonClient，优先级大于配置文件 -》 参数配置
     * @author kuangyoubo
     * @date 2019-06-21 12:15
     */
    private String clientBeanName;

    public String getClientBeanName() {
        return clientBeanName;
    }

    public void setClientBeanName(String clientBeanName) {
        this.clientBeanName = clientBeanName;
    }

    public boolean useInjectRedissonClient() {
        if (clientBeanName == null || clientBeanName.isEmpty()) {
            return false;
        }
        return true;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean useConfigParameter() {
        return CLUSTER.equals(mode) || SENTINEL.equals(mode);
    }

    public Config getClusterOrSentinelConfig() {
        Config config = new Config();

        if( CLUSTER.equals(mode) ) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();

            BeanUtils.copyProperties(this.cluster, clusterServersConfig, ClusterServersConfig.class);

            this.cluster.getNodeAddresses().parallelStream().forEach( node -> {
                clusterServersConfig.addNodeAddress( node.toString() );
            });
        }
        else if( SENTINEL.equals(mode) ) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();

            BeanUtils.copyProperties(this.sentinel, sentinelServersConfig, SentinelServersConfig.class);

            this.sentinel.getSentinelAddresses().parallelStream().forEach( node -> {
                sentinelServersConfig.addSentinelAddress( node.toString() );
            });

        }

        return config;
    }

    public void setCluster(ClusterServersConfig cluster) {
        this.cluster = cluster;
    }

    public void setSentinel(SentinelServersConfig sentinel) {
        this.sentinel = sentinel;
    }
    //add end 20190621

    public boolean useConfigFile() {
        if (configPath == null || configPath.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isYAMLConfig() {
        if (useConfigFile()) {
            return configPath.toLowerCase().endsWith("yaml");
        }
        return false;
    }

    public boolean isJSONConfig() {
        if (useConfigFile()) {
            return configPath.toLowerCase().endsWith("json");
        }
        return false;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    private String configPath;
    private String ip = "127.0.0.1";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean hasPassword() {
        return getPassword() != null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Integer port = 6379;
    private String password;

        /*
        * setConnectionPoolSize(32).setConnectionMinimumIdleSize(16);
        * */

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public Integer getMinimumIdleSize() {
        return minimumIdleSize;
    }

    public void setMinimumIdleSize(Integer minimumIdleSize) {
        this.minimumIdleSize = minimumIdleSize;
    }

    private Integer poolSize = 32;
    private Integer minimumIdleSize=16;

    @Override
    public String toString() {
        return "redis://" + getIp() + ":" + getPort();
    }
}
