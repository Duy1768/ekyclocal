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
public class SignatureValidationFilter
        extends OncePerRequestFilter {

    private final SignatureProperties signatureProperties;

    private final ObjectMapper objectMapper;

    private final MultipartResolver multipartResolver;

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

            writeErrorResponse(
                    response,
                    ResponseCode.MISSING_HEADER);

            return;
        }

        String body = "";

        if (multipartResolver.isMultipart(request)) {

            MultipartHttpServletRequest multipartRequest =
                    multipartResolver.resolveMultipart(request);

            body =
                    "fullName=" + multipartRequest.getParameter("fullName")
                            + "&idNumber=" + multipartRequest.getParameter("idNumber")
                            + "&phone=" + multipartRequest.getParameter("phone")
                            + "&email=" + multipartRequest.getParameter("email");
        }

        String plainText =
                body +
                        requestId +
                        requestDateTime;

        String generatedSignature =
                HmacUtil.sign(
                        plainText,
                        signatureProperties.getSecretKey());

        log.info("body={}", body);
        log.info("plainText={}", plainText);
        log.info("generatedSignature={}", generatedSignature);
        log.info("receivedSignature={}", signature);

        if (!generatedSignature.equals(signature)) {

            writeErrorResponse(
                    response,
                    ResponseCode.INVALID_SIGNATURE);

            return;
        }

        filterChain.doFilter(
                request,
                response);
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            ResponseCode responseCode)
            throws IOException {

        response.setStatus(HttpStatus.OK.value());

        BaseResponse<Void> errorResponse =
                BaseResponse.<Void>builder()
                        .responseCode(responseCode.getCode())
                        .responseMessage(responseCode.getMessage())
                        .responseId(
                                MDC.get(HeaderConstant.MDC_REQUEST_ID))
                        .requestTime(
                                LocalDateTime.now().toString())
                        .build();

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(
                response.getWriter(),
                errorResponse);
    }
}