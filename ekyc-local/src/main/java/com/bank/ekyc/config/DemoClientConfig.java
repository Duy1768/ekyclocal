package com.bank.ekyc.config;

import com.bank.ekyc.config.properties.DemoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class DemoClientConfig {

    private final WebClientConfig webClientConfig;

    private final DemoProperties demoProperties;

    @Bean
    public WebClient demoWebClient() {

        return webClientConfig.buildWebClient(

                demoProperties.getPool().getName(),
                demoProperties.getPool().getMaxConnections(),
                demoProperties.getPool().getPendingAcquireTimeout(),

                demoProperties.getBaseUrl(),
                demoProperties.getApiKey(),

                demoProperties.getTimeout().getConnect(),
                demoProperties.getTimeout().getResponse(),
                demoProperties.getTimeout().getRead(),
                demoProperties.getTimeout().getWrite()

        );

    }

}