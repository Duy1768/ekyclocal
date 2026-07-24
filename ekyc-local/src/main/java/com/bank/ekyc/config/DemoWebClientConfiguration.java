package com.bank.ekyc.config;

import com.bank.ekyc.config.properties.DemoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DemoProperties.class)
public class DemoWebClientConfiguration {

    private final WebClientConfig webClientConfig;
    private final DemoProperties properties;

    @Bean("demoWebClient")
    public WebClient demoWebClient() {
        return webClientConfig.buildWebClient(
                properties.getPool().getName(),
                properties.getPool().getMaxConnections(),
                properties.getPool().getPendingAcquireTimeout(),
                properties.getBaseUrl(),
                properties.getApiKey(),
                properties.getTimeout().getConnect(),
                properties.getTimeout().getResponse(),
                properties.getTimeout().getRead(),
                properties.getTimeout().getWrite()
        );
    }
}
