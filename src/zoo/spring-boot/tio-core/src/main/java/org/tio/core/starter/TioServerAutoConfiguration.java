package org.tio.core.starter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.tio.cluster.redisson.RedissonTioClusterTopic;
import org.tio.core.intf.GroupListener;
import org.tio.core.stat.IpStatListener;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;


/**
 * @author fanpan26
 * @author yangjian
 * */
@Configuration
@Import(TioServerInitializerConfiguration.class)
@ConditionalOnBean(TioServerMarkerConfiguration.Marker.class)
@EnableConfigurationProperties({
        TioServerProperties.class,
        TioServerClusterProperties.class,
        TioServerClusterProperties.RedisConfig.class,
        TioServerSslProperties.class})
public class TioServerAutoConfiguration {

    /**
     *  cluster topic channel
     * */
    private static final String CLUSTER_TOPIC_CHANNEL = "tio_server_spring_boot_starter";

    @Autowired(required = false)
    private ServerAioHandler serverAioHandler;

    @Autowired(required = false)
    private IpStatListener ipStatListener;

    @Autowired(required = false)
    private GroupListener groupListener;

    @Autowired(required = false)
    private ServerAioListener serverAioListener;

    @Autowired
    private TioServerClusterProperties clusterProperties;

    @Autowired
    private TioServerClusterProperties.RedisConfig redisConfig;

    @Autowired
    private TioServerProperties serverProperties;

    @Autowired
    private TioServerSslProperties serverSslProperties;

    @Autowired(required = false)
    private RedissonTioClusterTopic redissonTioClusterTopic;

    /**
     * Tio Server bootstrap
     * */
    @Bean
    public TioServerBootstrap webSocketServerBootstrap() {
        return new TioServerBootstrap(
                serverProperties,
                clusterProperties,
                serverSslProperties,
                redissonTioClusterTopic,
                ipStatListener,
                groupListener,
                serverAioHandler,
                serverAioListener);
    }

    @Bean
    public ServerGroupContext serverGroupContext(TioServerBootstrap bootstrap){
        return bootstrap.getServerGroupContext();
    }

    @Bean
    @ConditionalOnProperty(value = "tio.core.cluster.enabled",havingValue = "true",matchIfMissing = true)
    public RedisInitializer redisInitializer(){
        return new RedisInitializer(redisConfig);
    }


    /**
     *  RedissonTioClusterTopic  with  RedisInitializer
     * */
    @Bean
    @ConditionalOnBean(RedisInitializer.class)
    public RedissonTioClusterTopic redissonTioClusterTopic(RedisInitializer redisInitializer) {
        return new RedissonTioClusterTopic(CLUSTER_TOPIC_CHANNEL,redisInitializer.getRedissonClient());
    }
}
