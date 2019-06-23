package org.tio.core.starter.annotation;

import org.tio.core.starter.TioServerAutoConfiguration;
import org.tio.core.starter.TioServerMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 此注解用于启用 Tio Server 服务，有关配置请参考 {@link TioServerAutoConfiguration}
 * @author yangjian
 * @author fanpan26
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TioServerMarkerConfiguration.class)
public @interface EnableTioServerServer {
}
