package com.bank.ekyc.config;

import com.bank.ekyc.config.properties.ThirdPartyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ThirdPartyProperties.class)
public class RegexWebClientConfiguration {

    private final WebClientConfig webClientConfig;
    private final ThirdPartyProperties properties;

    @Bean("regexWebClient")
    public WebClient regexWebClient() {
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
