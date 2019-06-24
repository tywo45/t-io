package org.tio.common.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Responsible for adding in a marker bean to activate TioServerAutoConfigurations.
 * (TioServer,TioWebSocketServer,TioHttpServer etc)
 * @author fanpan26
 */
@Configuration
public class TioServerMarkerConfiguration {
    class Marker {
    }

    @Bean
    public Marker tioServerMarkBean() {
        return new Marker();
    }
}
