package bel.dm.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ServerConfig {
    @Bean
    public Integer getInteger() {
        return 0;
    }

    @Bean
    public Double getDouble() {return 0.0;}
}
