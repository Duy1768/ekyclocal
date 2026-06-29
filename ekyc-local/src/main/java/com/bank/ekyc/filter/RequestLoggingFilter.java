package com.bank.ekyc.filter;

import com.bank.ekyc.common.constant.HeaderConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestId =
                request.getHeader(HeaderConstant.REQUEST_ID);

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(HeaderConstant.MDC_REQUEST_ID, requestId);
        response.setHeader(HeaderConstant.REQUEST_ID, requestId);

        long startTime = System.currentTimeMillis();

        try {

            log.info(
                    "step=request_received method={} uri={} remoteAddress={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());

            filterChain.doFilter(
                    request,
                    response);

            log.info(
                    "step=request_completed status={} durationMs={}",
                    response.getStatus(),
                    System.currentTimeMillis() - startTime);

        } catch (Exception exception) {

            log.error(
                    "step=request_failed status={} durationMs={} exceptionType={}",
                    response.getStatus(),
                    System.currentTimeMillis() - startTime,
                    exception.getClass().getSimpleName(),
                    exception);

            throw exception;

        } finally {

            MDC.remove(HeaderConstant.MDC_REQUEST_ID);
        }
    }
}
