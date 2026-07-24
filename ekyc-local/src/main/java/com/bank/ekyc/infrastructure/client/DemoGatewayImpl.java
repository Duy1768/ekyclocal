package com.bank.ekyc.infrastructure.client;

import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.exception.BusinessException;
import com.bank.ekyc.common.exception.RetryableBusinessException;
import com.bank.ekyc.domain.gateway.DemoGateway;
import com.bank.ekyc.infrastructure.external.request.DemoRequest;
import com.bank.ekyc.infrastructure.external.response.DemoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Component
@Slf4j
public class DemoGatewayImpl implements DemoGateway {

    private static final String SUCCESS_CODE = "00";

    private final WebClient demoWebClient;

    public DemoGatewayImpl(
            @Qualifier("demoWebClient") WebClient demoWebClient) {
        this.demoWebClient = demoWebClient;
    }

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
                .flatMap(this::validateResponse)
                .retryWhen(retrySpec())
                .onErrorMap(
                        RetryableBusinessException.class,
                        ex -> new BusinessException(ResponseCode.UNKNOWN_ERROR)
                )
                .block();
    }

    private Mono<DemoResponse> validateResponse(DemoResponse response) {

        if (SUCCESS_CODE.equals(response.getResponseCode())) {
            return Mono.just(response);
        }

        if (Boolean.TRUE.equals(response.getRetryable())) {
            return Mono.error(
                    new RetryableBusinessException(
                            response.getResponseCode(),
                            response.getResponseMessage()));
        }

        return Mono.error(
                new BusinessException(ResponseCode.UNKNOWN_ERROR));
    }

    private RetryBackoffSpec retrySpec() {

        return Retry.backoff(3, Duration.ofSeconds(2))
                .filter(RetryableBusinessException.class::isInstance)
                .doBeforeRetry(signal ->
                        log.warn(
                                "Retry lần {} - responseCode={}, message={}",
                                signal.totalRetries() + 1,
                                ((RetryableBusinessException) signal.failure()).getResponseCode(),
                                signal.failure().getMessage()));
    }

}