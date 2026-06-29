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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class SignatureValidationFilter extends OncePerRequestFilter {

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

        if (requestId == null
                || requestId.isBlank()
                || requestDateTime == null
                || requestDateTime.isBlank()
                || signature == null
                || signature.isBlank()) {

            log.warn(
                    "step=signature_validation_failed reason=missing_header");

            writeErrorResponse(
                    response,
                    ResponseCode.MISSING_HEADER);

            return;
        }

        String body = buildRequestBody(request);

        String plainText =
                body + requestId + requestDateTime;

        String generatedSignature =
                HmacUtil.sign(
                        plainText,
                        signatureProperties.getSecretKey());

        log.info("body={}", body);
        log.info("plainText={}", plainText);
        log.info("generatedSignature={}", generatedSignature);
        log.info("receivedSignature={}", signature);

        if (!generatedSignature.equals(signature)) {

            log.warn(
                    "step=signature_validation_failed reason=invalid_signature");

            writeErrorResponse(
                    response,
                    ResponseCode.INVALID_SIGNATURE);

            return;
        }

        log.info(
                "step=signature_validation_success");

        filterChain.doFilter(
                request,
                response);
    }

    private String buildRequestBody(
            HttpServletRequest request) {

        String uri = request.getRequestURI();

        if ("/api/v1/customer".equals(uri)) {

            return "fullName="
                    + request.getParameter("fullName")
                    + "&idNumber="
                    + request.getParameter("idNumber")
                    + "&phone="
                    + request.getParameter("phone")
                    + "&email="
                    + request.getParameter("email");
        }

        if ("/api/v1/customer/face-compare".equals(uri)) {

            return "customerCode="
                    + request.getParameter("customerCode");
        }

        return "";
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            ResponseCode responseCode)
            throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        BaseResponse<Void> errorResponse =
                BaseResponse.<Void>builder()
                        .responseCode(responseCode.getCode())
                        .responseMessage(responseCode.getMessage())
                        .responseId(
                                MDC.get(HeaderConstant.MDC_REQUEST_ID))
                        .requestTime(LocalDateTime.now().toString())
                        .build();

        objectMapper.writeValue(
                response.getWriter(),
                errorResponse);
    }
}