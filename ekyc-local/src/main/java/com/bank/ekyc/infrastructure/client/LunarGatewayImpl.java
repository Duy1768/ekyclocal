package com.bank.ekyc.infrastructure.client;

import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.exception.BusinessException;
import com.bank.ekyc.domain.gateway.LunarGateway;
import com.bank.ekyc.infrastructure.external.response.TodayResponse;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LunarGatewayImpl implements LunarGateway {

    private final WebClient lunarWebClient;

    @Override
    public TodayResponse getToday() {

        long startTime = System.currentTimeMillis();

        try {

            TodayResponse response = lunarWebClient

                    .get()

                    .uri("/api/today")

                    .retrieve()

                    .onStatus(HttpStatusCode::is4xxClientError,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {

                                        log.error("step=third_party_client_error status={} body={}",
                                                clientResponse.statusCode(),
                                                body);

                                        return reactor.core.publisher.Mono.error(
                                                new BusinessException(
                                                        ResponseCode.THIRD_PARTY_CLIENT_ERROR));
                                    }))

                    .onStatus(HttpStatusCode::is5xxServerError,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {

                                        log.error("step=third_party_server_error status={} body={}",
                                                clientResponse.statusCode(),
                                                body);

                                        return reactor.core.publisher.Mono.error(
                                                new BusinessException(
                                                        ResponseCode.THIRD_PARTY_SERVER_ERROR));
                                    }))

                    .bodyToMono(TodayResponse.class)

                    .retryWhen(

                            Retry.backoff(3, Duration.ofSeconds(2))

                                    .filter(this::isTimeoutException)

                    )

                    .onErrorMap(this::mapThirdPartyException)

                    .block();

            if (response == null) {

                throw new BusinessException(
                        ResponseCode.THIRD_PARTY_INVALID_RESPONSE);

            }

            if (!Boolean.TRUE.equals(response.getSuccess())) {

                throw new BusinessException(
                        ResponseCode.THIRD_PARTY_INVALID_RESPONSE);

            }

            long duration = System.currentTimeMillis() - startTime;

            log.info("=============== THIRD PARTY SUCCESS ===============");

            log.info("RequestId : {}", MDC.get("requestId"));

            log.info("Duration  : {} ms", duration);

            log.info("Success   : {}", response.getSuccess());

            log.info("===================================================");

            return response;

        } catch (BusinessException ex) {

            long duration = System.currentTimeMillis() - startTime;

            log.error("=============== THIRD PARTY FAILED ===============");

            log.error("RequestId : {}", MDC.get("requestId"));

            log.error("Duration  : {} ms", duration);

            log.error("ResponseCode : {}", ex.getResponseCode().getCode());

            log.error("Message      : {}", ex.getMessage());

            log.error("==================================================");

            throw ex;

        } catch (Exception ex) {

            long duration = System.currentTimeMillis() - startTime;

            log.error("=============== THIRD PARTY UNKNOWN ERROR ========");

            log.error("RequestId : {}", MDC.get("requestId"));

            log.error("Duration  : {} ms", duration);

            log.error("Exception : {}", ex.getClass().getSimpleName());

            log.error("Message   : {}", ex.getMessage(), ex);

            log.error("==================================================");

            throw new BusinessException(
                    ResponseCode.THIRD_PARTY_UNKNOWN_ERROR);

        }

    }

    private Throwable mapThirdPartyException(Throwable ex) {

        if (ex instanceof BusinessException) {

            return ex;

        }

        ResponseCode responseCode = resolveTimeoutResponseCode(ex);

        if (responseCode == null) {

            return ex;

        }

        log.error(
                "step=third_party_timeout responseCode={} responseMessage={} exceptionType={} message={}",
                responseCode.getCode(),
                responseCode.getMessage(),
                ex.getClass().getSimpleName(),
                ex.getMessage());

        return new BusinessException(responseCode);

    }

    private ResponseCode resolveTimeoutResponseCode(Throwable ex) {

        if (hasCause(ex, ConnectTimeoutException.class)) {

            return ResponseCode.THIRD_PARTY_CONNECTION_ERROR;

        }

        if (hasCause(ex, ReadTimeoutException.class)
                || hasCause(ex, WriteTimeoutException.class)
                || hasCause(ex, TimeoutException.class)) {

            return ResponseCode.THIRD_PARTY_TIMEOUT;

        }

        return null;

    }

    private boolean isTimeoutException(Throwable ex) {

        return resolveTimeoutResponseCode(ex) != null;

    }

    private boolean hasCause(
            Throwable ex,
            Class<? extends Throwable> exceptionType) {

        Throwable current = ex;

        while (current != null) {

            if (exceptionType.isInstance(current)) {

                return true;

            }

            if (current instanceof WebClientRequestException
                    && exceptionType.isInstance(current.getCause())) {

                return true;

            }

            current = current.getCause();

        }

        return false;

    }

}
