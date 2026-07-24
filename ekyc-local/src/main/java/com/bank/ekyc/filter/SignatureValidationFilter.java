package com.bank.ekyc.filter;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.common.util.HmacUtil;
import com.bank.ekyc.config.properties.SignatureProperties;
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
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class SignatureValidationFilter extends OncePerRequestFilter {

    private final SignatureProperties signatureProperties;
    private final ObjectMapper objectMapper;

    /**
     * Chỉ validate signature cho các API eKYC.
     */
    private static final Set<String> EKYC_SIGNATURE_APIS = Set.of(
            "/api/v1/customer/face-compare",
            "/api/v1/customer/liveness"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        return !EKYC_SIGNATURE_APIS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        validateEkycSignature(
                request,
                response,
                filterChain);
    }

    /**
     * PlainText = body + requestId + requestDateTime
     */
    private void validateEkycSignature(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {

        String requestId = firstNonBlank(
                request.getHeader(HeaderConstant.REQUEST_ID),
                request.getParameter("requestId"));

        String requestDateTime = firstNonBlank(
                request.getHeader(HeaderConstant.REQUEST_TIME),
                request.getParameter("requestDateTime"));

        String signature = firstNonBlank(
                request.getHeader(HeaderConstant.JWS_SIGNATURE),
                request.getParameter("jwsSignature"));

        if (requestId == null
                || requestId.isBlank()
                || requestDateTime == null
                || requestDateTime.isBlank()
                || signature == null
                || signature.isBlank()) {

            log.warn("step=signature_validation_failed reason=missing_header");

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

            log.warn("step=signature_validation_failed reason=invalid_signature");

            writeErrorResponse(
                    response,
                    ResponseCode.INVALID_SIGNATURE);

            return;
        }

        log.info("step=signature_validation_success");

        filterChain.doFilter(
                request,
                response);
    }

    private String buildRequestBody(HttpServletRequest request) {

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

        if ("/api/v1/customer/liveness".equals(uri)) {
            return "";
        }

        return "";
    }

    private String firstNonBlank(
            String primary,
            String fallback) {

        if (primary != null && !primary.isBlank()) {
            return primary;
        }

        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }

        return null;
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
                        .responseId(MDC.get(HeaderConstant.MDC_REQUEST_ID))
                        .requestTime(LocalDateTime.now().toString())
                        .build();

        objectMapper.writeValue(
                response.getWriter(),
                errorResponse);
    }
}