package org.tio.websocket.starter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.cluster.TioClusterConfig;
import org.tio.cluster.redisson.RedissonTioClusterTopic;
import org.tio.core.intf.GroupListener;
import org.tio.core.stat.IpStatListener;
import org.tio.server.ServerGroupContext;
import org.tio.utils.Threads;
import org.tio.websocket.server.WsServerAioListener;
import org.tio.websocket.server.WsServerConfig;
import org.tio.websocket.server.WsServerStarter;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;

/**
 * @author fanpan26
 * */
public final class TioWebSocketServerBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(TioWebSocketServerBootstrap.class);

    private static final String GROUP_CONTEXT_NAME = "tio-websocket-spring-boot-starter";

    private TioWebSocketServerProperties serverProperties;
    private TioWebSocketServerClusterProperties clusterProperties;
    private TioWebSocketServerSslProperties serverSslProperties;
    private RedissonTioClusterTopic redissonTioClusterTopic;
    private WsServerConfig wsServerConfig;
    private TioClusterConfig clusterConfig;
    private WsServerStarter wsServerStarter;
    private ServerGroupContext serverGroupContext;
    private IWsMsgHandler tioWebSocketMsgHandler;
    private IpStatListener ipStatListener;
    private GroupListener groupListener;
    private WsServerAioListener serverAioListener;

    public TioWebSocketServerBootstrap(TioWebSocketServerProperties serverProperties,
                                       TioWebSocketServerClusterProperties clusterProperties,
                                       TioWebSocketServerSslProperties serverSslProperties,
                                       RedissonTioClusterTopic redissonTioClusterTopic,
                                       IWsMsgHandler tioWebSocketMsgHandler,
                                       IpStatListener ipStatListener,
                                       GroupListener groupListener,
                                       WsServerAioListener serverAioListener,
                                       TioWebSocketClassScanner tioWebSocketClassScanner) {
        this.serverProperties = serverProperties;
        this.clusterProperties = clusterProperties;
        this.serverSslProperties = serverSslProperties;

        logger.debug(serverSslProperties.toString());
        if (redissonTioClusterTopic == null) {
            logger.info("cluster mod closed");
        }
        this.redissonTioClusterTopic = redissonTioClusterTopic;

        // IWsMsgHandler bean not found
        if (tioWebSocketClassScanner != null) {
            if (tioWebSocketMsgHandler == null) {
                tioWebSocketClassScanner.scanInstance(IWsMsgHandler.class, instance -> {
                    this.tioWebSocketMsgHandler = (IWsMsgHandler) instance;
                });
            } else {
                this.tioWebSocketMsgHandler = tioWebSocketMsgHandler;
            }

            if (ipStatListener == null) {
                tioWebSocketClassScanner.scanInstance(IpStatListener.class, instance -> {
                    this.ipStatListener = (IpStatListener) instance;
                });
            } else {
                this.ipStatListener = ipStatListener;
            }
            if (groupListener == null) {
                tioWebSocketClassScanner.scanInstance(GroupListener.class, instance -> {
                    this.groupListener = (GroupListener) instance;
                });
            } else {
                this.groupListener = groupListener;
            }
            if (serverAioListener == null) {
                tioWebSocketClassScanner.scanInstance(WsServerAioListener.class, instance -> {
                    this.serverAioListener = (WsServerAioListener) instance;
                });
            } else {
                this.serverAioListener = serverAioListener;
            }
        } else {
            this.tioWebSocketMsgHandler = tioWebSocketMsgHandler;
            this.ipStatListener = ipStatListener;
            this.groupListener = groupListener;
        }
        afterSetProperties(tioWebSocketClassScanner);
    }

    private void afterSetProperties(TioWebSocketClassScanner scanner){
        if (this.tioWebSocketMsgHandler == null) {
            throw new TioWebSocketMsgHandlerNotFoundException();
        }
        if (this.ipStatListener == null){
            logger.warn("no bean type of IpStatListener found");
        }
        if (this.groupListener == null){
            logger.warn("no bean type of GroupListener found");
        }
        if (scanner!=null){
            scanner.destroy();
        }
    }


    public ServerGroupContext getServerGroupContext() {
        return serverGroupContext;
    }

    public void contextInitialized() {
        logger.info("initialize tio websocket server");
        try {
            initTioWebSocketConfig();
            initTioWebSocketServer();
            initTioWebSocketServerGroupContext();

            start();
        }
        catch (Throwable e) {
            logger.error("Cannot bootstrap tio websocket server :", e);
            throw new RuntimeException("Cannot bootstrap tio websocket server :", e);
        }
    }

    private void initTioWebSocketConfig() {
        this.wsServerConfig = new WsServerConfig(serverProperties.getPort());
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

    private void initTioWebSocketServer() throws Exception {
        wsServerStarter = new WsServerStarter(wsServerConfig,
                tioWebSocketMsgHandler,
                new TioWebSocketServerDefaultUuid(1L,1L),
                Threads.getTioExecutor(),
                Threads.getGroupExecutor());
    }

    private void initTioWebSocketServerGroupContext() {
        serverGroupContext = wsServerStarter.getServerGroupContext();
        serverGroupContext.setName(GROUP_CONTEXT_NAME);
        if (ipStatListener != null) {
            serverGroupContext.setIpStatListener(ipStatListener);
        }
        if(serverAioListener != null) {
            serverGroupContext.setServerAioListener(this.serverAioListener);
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
        wsServerStarter.start();
    }
}
