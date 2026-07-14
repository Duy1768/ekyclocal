package com.bank.ekyc.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class WebClientConfig {

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "x-api-key",
            "api-key"
    );

    public WebClient buildWebClient(
            String poolName,
            Integer maxConnections,
            Integer pendingAcquireTimeout,

            String baseUrl,
            String apiKey,

            Integer connectTimeout,
            Integer responseTimeout,
            Integer readTimeout,
            Integer writeTimeout) {

        ConnectionProvider provider = ConnectionProvider.builder(poolName)

                .maxConnections(maxConnections)

                .pendingAcquireTimeout(
                        Duration.ofSeconds(pendingAcquireTimeout))

                .build();

        HttpClient httpClient = HttpClient.create(provider)

                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        connectTimeout)

                .responseTimeout(
                        Duration.ofMillis(responseTimeout))

                .doOnConnected(conn -> conn

                        .addHandlerLast(
                                new ReadTimeoutHandler(readTimeout))

                        .addHandlerLast(
                                new WriteTimeoutHandler(writeTimeout)));

        return WebClient.builder()

                .baseUrl(baseUrl)

                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)

                .defaultHeader(
                        "x-api-key",
                        apiKey)

                .clientConnector(
                        new ReactorClientHttpConnector(httpClient))

                .filter(requestLoggingFilter())

                .filter(responseLoggingFilter())

                .build();

    }

    private ExchangeFilterFunction requestLoggingFilter() {

        return ExchangeFilterFunction.ofRequestProcessor(request -> {

            String requestId = MDC.get("requestId");

            if (requestId == null) {
                requestId = UUID.randomUUID().toString();
            }

            log.info("================ OUTGOING REQUEST ================");

            log.info("RequestId : {}", requestId);

            log.info("Method    : {}", request.method());

            log.info("URL       : {}", request.url());

            request.headers().forEach((name, values) -> {

                if (SENSITIVE_HEADERS.contains(name.toLowerCase())) {

                    log.info("{} : ********", name);

                } else {

                    log.info("{} : {}", name, values);

                }

            });

            log.info("==================================================");

            return Mono.just(request);

        });

    }

    private ExchangeFilterFunction responseLoggingFilter() {

        return ExchangeFilterFunction.ofResponseProcessor(response -> {

            log.info("================ RESPONSE ================");

            log.info("Status : {}", response.statusCode());

            log.info("==========================================");

            return Mono.just(response);

        });

    }

}