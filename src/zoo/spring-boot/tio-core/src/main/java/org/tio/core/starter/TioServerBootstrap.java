package org.tio.core.starter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.cluster.TioClusterConfig;
import org.tio.cluster.redisson.RedissonTioClusterTopic;
import org.tio.common.starter.TioServerMsgHandlerNotFoundException;
import org.tio.core.intf.GroupListener;
import org.tio.core.starter.configuration.TioServerClusterProperties;
import org.tio.core.starter.configuration.TioServerProperties;
import org.tio.core.starter.configuration.TioServerSslProperties;
import org.tio.core.stat.IpStatListener;
import org.tio.server.ServerGroupContext;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

import java.io.IOException;

/**
 * @author fanpan26
 * @author yangjian
 * */
public final class TioServerBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(TioServerBootstrap.class);

    private static final String GROUP_CONTEXT_NAME = "tio-server-spring-boot-starter";

    private TioServerProperties serverProperties;
    private TioServerClusterProperties clusterProperties;
    private TioServerSslProperties serverSslProperties;
    private RedissonTioClusterTopic redissonTioClusterTopic;
    private TioClusterConfig clusterConfig;
    private TioServer tioServer;
    private ServerGroupContext serverGroupContext;
    private ServerAioHandler serverAioHandler;
    private IpStatListener ipStatListener;
    private GroupListener groupListener;
    private ServerAioListener serverAioListener;

    public TioServerBootstrap(TioServerProperties serverProperties,
                              TioServerClusterProperties clusterProperties,
                              TioServerSslProperties serverSslProperties,
                              RedissonTioClusterTopic redissonTioClusterTopic,
                              IpStatListener ipStatListener,
                              GroupListener groupListener,
                              ServerAioHandler serverAioHandler,
                              ServerAioListener serverAioListener) {
        this.serverProperties = serverProperties;
        this.clusterProperties = clusterProperties;
        this.serverSslProperties = serverSslProperties;

        logger.debug(serverSslProperties.toString());
        if (redissonTioClusterTopic == null) {
            logger.info("cluster mod closed");
        }
        this.redissonTioClusterTopic = redissonTioClusterTopic;
        this.ipStatListener = ipStatListener;
        this.groupListener = groupListener;
        this.serverAioListener = serverAioListener;
        this.serverAioHandler = serverAioHandler;
        afterSetProperties();
    }

    private void afterSetProperties(){
        if (this.serverAioHandler == null) {
            throw new TioServerMsgHandlerNotFoundException();
        }
        if (this.ipStatListener == null){
            logger.warn("no bean type of IpStatListener found");
        }
        if (this.groupListener == null){
            logger.warn("no bean type of GroupListener found");
        }
    }


    public ServerGroupContext getServerGroupContext() {
        return serverGroupContext;
    }

    public void contextInitialized() {
        logger.info("initialize tio websocket server");
        try {
            initTioServerConfig();
            initTioServerGroupContext();
            initTioServer();
            start();
        }
        catch (Throwable e) {
            logger.error("Cannot bootstrap tio server :", e);
            throw new RuntimeException("Cannot bootstrap tio server :", e);
        }
    }

    private void initTioServerConfig() {

        if (redissonTioClusterTopic != null && clusterProperties.isEnabled()) {
            this.clusterConfig = new TioClusterConfig(redissonTioClusterTopic);
            this.clusterConfig.setCluster4all(clusterProperties.isAll());
            this.clusterConfig.setCluster4bsId(true);
            this.clusterConfig.setCluster4channelId(clusterProperties.isChannel());
            this.clusterConfig.setCluster4group(clusterProperties.isGroup());
            this.clusterConfig.setCluster4ip(clusterProperties.isIp());
            this.clusterConfig.setCluster4user(clusterProperties.isUser());
        }
    }

    private void initTioServer()
    {
        this.tioServer = new TioServer(serverGroupContext);
    }

    private void initTioServerGroupContext()
    {
        serverGroupContext = new ServerGroupContext(GROUP_CONTEXT_NAME, serverAioHandler, serverAioListener);
        if (ipStatListener != null) {
            serverGroupContext.setIpStatListener(ipStatListener);
            serverGroupContext.ipStats.addDurations(serverProperties.getIpStatDurations());
        }
        if(serverAioListener != null) {
            serverGroupContext.setServerAioListener(serverAioListener);
        }
        if (groupListener != null) {
            serverGroupContext.setGroupListener(groupListener);
        }
        if (serverProperties.getHeartbeatTimeout() > 0) {
            serverGroupContext.setHeartbeatTimeout(serverProperties.getHeartbeatTimeout());
        }
        //cluster config
        if (clusterConfig != null) {
            serverGroupContext.setTioClusterConfig(clusterConfig);
        }
        //ssl config
        if (serverSslProperties.isEnabled()){
            try {
                serverGroupContext.useSsl(serverSslProperties.getKeyStore(), serverSslProperties.getTrustStore(), serverSslProperties.getPassword());
            }catch (Exception e){
                //catch and log
                logger.error("init ssl config error",e);
            }
        }
    }

    private void start() throws IOException {
        tioServer.start(serverProperties.getIp(), serverProperties.getPort());
    }
}
