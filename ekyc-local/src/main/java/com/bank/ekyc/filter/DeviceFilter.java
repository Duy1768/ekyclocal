package com.bank.ekyc.filter;

import com.bank.ekyc.application.service.DeviceService;
import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.context.DeviceContextHolder;
import com.bank.ekyc.domain.entity.Device;
import com.bank.ekyc.resolver.DeviceHeaderResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DeviceFilter extends OncePerRequestFilter {

    private static final Set<String> DEVICE_APIS = Set.of(
            "/api/by-time"
    );

    private final DeviceHeaderResolver resolver;

    private final DeviceService deviceService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return !DEVICE_APIS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String hardwareUuid =
                    request.getHeader(HeaderConstant.HARDWARE_UUID);

            if (hardwareUuid != null && !hardwareUuid.isBlank()) {

                Device device =
                        resolver.resolve(request);

                Device saved =
                        deviceService.saveOrUpdate(device);

                DeviceContextHolder.setCurrentDevice(saved);
            }

            filterChain.doFilter(request, response);

        } finally {

            DeviceContextHolder.clear();

        }

    }

}