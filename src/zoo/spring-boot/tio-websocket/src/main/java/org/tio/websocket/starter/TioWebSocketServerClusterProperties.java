package org.tio.websocket.starter;

import static org.tio.websocket.starter.TioWebSocketServerClusterProperties.PREFIX;

import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.tio.utils.hutool.StrUtil;

/**
 * @author fanpan26
 * */
@ConfigurationProperties(PREFIX)
public class TioWebSocketServerClusterProperties {
    public static final String PREFIX = "tio.websocket.cluster";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 是否开启集群 默认为 false
     * */
    private boolean enabled = false;
    /**
     * 群组是否集群（同一个群组是否会分布在不同的机器上），false:不集群，默认不集群
     */
    private boolean	group = false;
    /**
     * 用户是否集群（同一个用户是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean	user = true;
    /**
     * ip是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean	ip = true;
    /**
     * id是否集群（在A机器上的客户端是否可以通过channelId发消息给B机器上的客户端），false:不集群，默认集群<br>
     */
    private boolean	channel	= true;

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public boolean isIp() {
        return ip;
    }

    public void setIp(boolean ip) {
        this.ip = ip;
    }

    public boolean isChannel() {
        return channel;
    }

    public void setChannel(boolean channel) {
        this.channel = channel;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean bll) {
        this.all = bll;
    }

    /**
     * 所有连接是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
     */
    private boolean	all = true;

    @ConfigurationProperties("tio.websocket.cluster.redis")
    public static class RedisConfig {
    	
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
			if (StrUtil.isBlank(clientBeanName)){
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
			if (StrUtil.isBlank(this.mode)){
                return false;
            }
			return true;
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
		
		public boolean useConfigFile(){
            if (StrUtil.isBlank(configPath)){
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

        public boolean hasPassword(){
            return StrUtil.isNotBlank(getPassword());
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
}
