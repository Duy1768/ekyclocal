package com.bank.ekyc.config;

import com.bank.ekyc.config.properties.LunarProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LunarProperties.class)
public class LunarWebClientConfiguration {

    private final WebClientConfig webClientConfig;

    private final LunarProperties properties;

    @Bean("lunarWebClient")
    public WebClient lunarWebClient() {

        return webClientConfig.buildWebClient(

                "lunar-pool",

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
