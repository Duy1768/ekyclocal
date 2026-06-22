package com.bank.ekyc.filter;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.common.util.HmacUtil;
import com.bank.ekyc.config.SignatureProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class SignatureValidationFilter
        extends OncePerRequestFilter {

    private final SignatureProperties signatureProperties;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestId =
                request.getHeader(HeaderConstant.REQUEST_ID);

        String requestDateTime =
                request.getHeader(HeaderConstant.REQUEST_TIME);

        String signature =
                request.getHeader(HeaderConstant.JWS_SIGNATURE);

        if (requestId == null ||
                requestDateTime == null ||
                signature == null) {

            log.warn(
                    "step=signature_rejected reason=missing_header requestIdPresent={} requestDateTimePresent={} signaturePresent={}",
                    requestId != null,
                    requestDateTime != null,
                    signature != null);

            writeErrorResponse(
                    response,
                    ResponseCode.MISSING_HEADER);
            return;
        }

        log.info("step=signature_validation_started requestDateTime={}", requestDateTime);

        String body =
                new String(
                        request.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);

        log.info("step=request_body_read bodySizeBytes={}", body.getBytes(StandardCharsets.UTF_8).length);

        String plainText =
                body +
                        requestId +
                        requestDateTime;

        String generatedSignature =
                HmacUtil.sign(
                        plainText,
                signatureProperties.getSecretKey());

        if (!generatedSignature.equals(signature)) {

            log.warn("step=signature_rejected reason=invalid_signature");

            writeErrorResponse(
                    response,
                    ResponseCode.INVALID_SIGNATURE);
            return;
        }

        log.info("step=signature_validated");

        filterChain.doFilter(
                request,
                response);
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            ResponseCode responseCode)
            throws IOException {

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        BaseResponse<Void> errorResponse =
                BaseResponse.<Void>builder()
                        .responseCode(responseCode.getCode())
                        .responseMessage(responseCode.getMessage())
                        .responseId(MDC.get(HeaderConstant.MDC_REQUEST_ID))
                        .requestTime(LocalDateTime.now().toString())
                        .build();

        objectMapper.writeValue(
                response.getWriter(),
                errorResponse);
    }
}
