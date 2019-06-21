package org.tio.websocket.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.tio.utils.hutool.ClassUtil;

import java.util.Map;
import java.util.function.Consumer;

/**
 * If there are no {@link org.springframework.stereotype.Service} annotations or {@link WebSocketMsgHandler} annotations,
 * the {@link org.tio.websocket.server.handler} or {@link TioWebSocketMsgHandler} will be found by scanning packages
 *
 * @author fanpan26
 */
public class TioWebSocketClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(TioWebSocketClassScanner.class);
    private ApplicationContext applicationContext;

    public TioWebSocketClassScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void scanInstance(Class<?> beanClazz, Consumer<Object> consumer) {
        Map<String, Object> annotationMap = applicationContext.getBeansWithAnnotation(EnableTioWebSocketServer.class);
        Class applicationClazz = annotationMap.entrySet().iterator().next().getValue().getClass();
        String packageName = applicationClazz.getPackage().getName();

        try {
            ClassUtil.scanPackage(packageName, clazz -> {
                if (!clazz.isInterface()) {
                    if (beanClazz.isAssignableFrom(clazz)) {
                        try {
                            consumer.accept(clazz.newInstance());
                        } catch (Exception e) {
                            throw new RuntimeException("create new instance of "+clazz.getTypeName()+" failed");
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        catch (NoClassDefFoundError error){
            //ignored this error
            logger.warn( "NoClassDefFoundError:" + error.getMessage());
        }
    }

    public void destroy() {

    }
}
