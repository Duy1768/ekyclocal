package com.bank.ekyc.infrastructure.client;

import com.bank.ekyc.application.service.ThirdPartyCallHistoryService;
import com.bank.ekyc.common.util.HmacUtil;
import com.bank.ekyc.config.properties.SignatureProperties;
import com.bank.ekyc.context.DeviceContextHolder;
import com.bank.ekyc.domain.entity.Device;
import com.bank.ekyc.domain.entity.ThirdPartyCallHistory;
import com.bank.ekyc.domain.gateway.TransactionGateway;
import com.bank.ekyc.infrastructure.external.response.TransactionResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TransactionGatewayImpl implements TransactionGateway {

    private final WebClient webClient;
    private final ThirdPartyCallHistoryService historyService;
    private final SignatureProperties signatureProperties;

    public TransactionGatewayImpl(
            @Qualifier("regexWebClient") WebClient webClient,
            ThirdPartyCallHistoryService historyService,
            SignatureProperties signatureProperties) {

        this.webClient = webClient;
        this.historyService = historyService;
        this.signatureProperties = signatureProperties;
    }

    @Override
    public List<TransactionResponse> findByTransactionTime(LocalDateTime transactionTime) {

        LocalDateTime requestTime = LocalDateTime.now();

        Device device = DeviceContextHolder.getCurrentDevice();

        try {

            String transactionTimeValue = transactionTime.toString();

            String signature = HmacUtil.sign(
                    transactionTimeValue,
                    signatureProperties.getSecretKey());

            List<TransactionResponse> response =
                    webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/byFind")
                                    .queryParam("transactionTime", transactionTimeValue)
                                    .build())
                            .header("JWS-Signature", signature)
                            .retrieve()
                            .bodyToFlux(TransactionResponse.class)
                            .collectList()
                            .block();

            LocalDateTime responseTime = LocalDateTime.now();

            saveHistory(
                    device,
                    transactionTime,
                    requestTime,
                    responseTime,
                    "200",
                    "SUCCESS",
                    null
            );

            return response;

        } catch (WebClientResponseException ex) {

            LocalDateTime responseTime = LocalDateTime.now();

            saveHistory(
                    device,
                    transactionTime,
                    requestTime,
                    responseTime,
                    String.valueOf(ex.getStatusCode().value()),
                    "FAIL",
                    ex.getResponseBodyAsString()
            );

            throw ex;

        } catch (Exception ex) {

            LocalDateTime responseTime = LocalDateTime.now();

            saveHistory(
                    device,
                    transactionTime,
                    requestTime,
                    responseTime,
                    "500",
                    "FAIL",
                    ex.getMessage()
            );

            throw ex;
        }
    }

    @Override
    public List<TransactionResponse> getTransactions(Long lastTransactionId) {

        throw new UnsupportedOperationException("Not implemented yet.");

    }

    private void saveHistory(Device device,
                             LocalDateTime transactionTime,
                             LocalDateTime requestTime,
                             LocalDateTime responseTime,
                             String responseCode,
                             String status,
                             String errorMessage) {

        long duration = Duration.between(requestTime, responseTime).toMillis();

        ThirdPartyCallHistory history =
                ThirdPartyCallHistory.builder()
                        .deviceId(device == null ? null : device.getId())
                        .apiName("/api/byFind")
                        .transactionTime(transactionTime)
                        .requestTime(requestTime)
                        .responseTime(responseTime)
                        .durationMs(duration)
                        .responseCode(responseCode)
                        .status(status)
                        .errorMessage(errorMessage)
                        .build();

        historyService.save(history);
    }
}