package org.tio.core.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 负责添加一个标记，表示 Tio Server 已经启用，防止重复启用
 * {@link TioServerAutoConfiguration}
 * @author yangjian
 * @author fanpan26
 * */
@Configuration
public class TioServerMarkerConfiguration {

    @Bean
    public Marker tioWebSocketServerMarkBean() {
        return new Marker();
    }

    class Marker {
    }
}