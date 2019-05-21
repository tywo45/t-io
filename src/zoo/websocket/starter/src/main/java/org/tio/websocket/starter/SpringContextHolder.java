package org.tio.websocket.starter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.tio.server.ServerGroupContext;

/**
 * @author fanpan26
 * */
public final class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static TioWebSocketServerProperties getWebSocketServerProperties(){
        return applicationContext.getBean(TioWebSocketServerProperties.class);
    }

    public static TioWebSocketServerBootstrap getWebSocketServerBootstrap() {
        return applicationContext.getBean(TioWebSocketServerBootstrap.class);
    }

    public static ServerGroupContext getSerGorupContext(){
        return getWebSocketServerBootstrap().getServerGroupContext();
    }
}