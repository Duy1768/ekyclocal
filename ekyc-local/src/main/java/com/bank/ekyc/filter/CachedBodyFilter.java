package com.bank.ekyc.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CachedBodyFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String contentType = request.getContentType();

        return contentType != null
                && contentType.startsWith("multipart/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(
                new CachedBodyRequest(request),
                response);
    }

    private static class CachedBodyRequest
            extends HttpServletRequestWrapper {

        private final byte[] cachedBody;

        CachedBodyRequest(
                HttpServletRequest request)
                throws IOException {

            super(request);

            this.cachedBody =
                    StreamUtils.copyToByteArray(
                            request.getInputStream());
        }

        @Override
        public ServletInputStream getInputStream() {

            ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(cachedBody);

            return new ServletInputStream() {

                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(
                        ReadListener readListener) {
                }
            };
        }

        @Override
        public BufferedReader getReader() {

            return new BufferedReader(
                    new InputStreamReader(
                            getInputStream(),
                            StandardCharsets.UTF_8));
        }
    }
}