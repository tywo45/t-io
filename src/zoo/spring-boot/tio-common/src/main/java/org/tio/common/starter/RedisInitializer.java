package org.tio.common.starter;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.FstCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.tio.common.starter.configuration.TioRedisClusterProperties;

import java.io.IOException;
import java.net.URL;

/**
 * @author fanpan26
 */
public final class RedisInitializer {

    private static final Logger logger = LoggerFactory.getLogger(RedisInitializer.class);

    private RedissonClient redissonClient;
    private ApplicationContext applicationContext;
    private TioRedisClusterProperties redisConfig;

    public RedisInitializer(TioRedisClusterProperties redisConfig, ApplicationContext applicationContext) {
        this.redisConfig = redisConfig;
        this.applicationContext = applicationContext;

        initRedis();
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    public void shutdown() {
        if( redisConfig.useInjectRedissonClient() && !redissonClient.isShutdown() ) {
            redissonClient.shutdown();
        }
    }

    private URL getFileUri(String fileName) {
        return applicationContext.getClassLoader().getResource(fileName);
    }

    /**
     * @author kuangyoubo
     * @author fanpan26
     * 优先级
     * 通过名字注入 > 配置文件 > 参数配置 > 默认
     */
    private void initRedis() {
        if( redisConfig.useInjectRedissonClient() ) {
            logger.info("Get the RedissonClient through injection, Bean name is \"{}\"", redisConfig.getClientBeanName());

            try {
                redissonClient = applicationContext.getBean(redisConfig.getClientBeanName(), RedissonClient.class);
                return;
            } catch (BeansException e) {
                logger.warn("RedissonClient is not found, Recreate RedissonClient on configuration information.");
            }
        }

        /**
         * 优先级
         * 配置文件 > 参数配置 > 默认
         */
        Config config = getConfigByFile();
        if(config == null) {
            config = redisConfig.useConfigParameter() ? redisConfig.getClusterOrSentinelConfig() : getSingleServerConfig();
        }
        redissonClient = Redisson.create(config);
    }

    private Config getConfigByFile() {
        try {
            if (redisConfig.isJSONConfig()) {
                return Config.fromJSON(getFileUri(redisConfig.getConfigPath()));
            } else if (redisConfig.isYAMLConfig()) {
                return Config.fromYAML(getFileUri(redisConfig.getConfigPath()));
            }
        } catch (IOException e) {
            logger.error("init with file config error", e);
        }
        return null;
    }

    private Config getSingleServerConfig() {
        Config config = new Config();
        String address = redisConfig.toString();
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(address)
                .setConnectionPoolSize(redisConfig.getPoolSize())
                .setConnectionMinimumIdleSize(redisConfig.getMinimumIdleSize());
        if (redisConfig.hasPassword()) {
            singleServerConfig.setPassword(redisConfig.getPassword());
        }
        config.setCodec(new FstCodec());
        //默认情况下，看门狗的检查锁的超时时间是30秒钟
        config.setLockWatchdogTimeout(1000 * 30);
        return config;
    }

}
