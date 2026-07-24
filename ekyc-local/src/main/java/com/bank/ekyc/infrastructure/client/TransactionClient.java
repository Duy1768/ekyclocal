package com.bank.ekyc.infrastructure.client;

import com.bank.ekyc.filter.DeviceHeaderFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

public class TransactionClient {

    private final WebClient webClient;

    public TransactionClient() {

        this.webClient =
                WebClient.builder()
                        .baseUrl("http://localhost:8080")
                        .filter(DeviceHeaderFilter.deviceHeader())
                        .build();
    }

    public String getTransaction(LocalDateTime transactionTime) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/by-time")
                        .queryParam("transactionTime", transactionTime)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

}