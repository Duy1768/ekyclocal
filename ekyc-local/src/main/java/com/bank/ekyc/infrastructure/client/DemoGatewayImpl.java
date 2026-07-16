package com.bank.ekyc.infrastructure.client;

import com.bank.ekyc.common.exception.RetryableBusinessException;
import com.bank.ekyc.domain.gateway.DemoGateway;
import com.bank.ekyc.infrastructure.external.request.DemoRequest;
import com.bank.ekyc.infrastructure.external.response.DemoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoGatewayImpl implements DemoGateway {

    private final WebClient demoWebClient;

    @Override
    public DemoResponse call(String responseCode) {

        DemoRequest request = new DemoRequest();
        request.setResponseCode(responseCode);

        return demoWebClient

                .post()

                .uri("/mock")

                .bodyValue(request)

                .retrieve()

                .bodyToMono(DemoResponse.class)

                .flatMap(response -> {

                    if (Boolean.TRUE.equals(response.getRetryable())) {

                        return Mono.error(
                                new RetryableBusinessException(
                                        response.getResponseCode(),
                                        response.getResponseMessage()));
                    }

                    return Mono.just(response);

                })

                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))

                                .filter(ex ->
                                        ex instanceof RetryableBusinessException)

                                .doBeforeRetry(signal ->
                                        log.warn(
                                                "Retry lần {} - {}",
                                                signal.totalRetries() + 1,
                                                signal.failure().getMessage()))
                )

                .block();

    }

}