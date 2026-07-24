package com.bank.ekyc.resolver;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.domain.entity.Device;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DeviceHeaderResolver {

    public Device resolve(HttpServletRequest request) {

        return Device.builder()
                .hardwareUuid(request.getHeader(HeaderConstant.HARDWARE_UUID))
                .hostname(request.getHeader(HeaderConstant.HOSTNAME))
                .macAddress(request.getHeader(HeaderConstant.MAC_ADDRESS))
                .osName(request.getHeader(HeaderConstant.OS_NAME))
                .osVersion(request.getHeader(HeaderConstant.OS_VERSION))
                .appVersion(request.getHeader(HeaderConstant.APP_VERSION))
                .ipAddress(getClientIp(request))
                .build();
    }

    private String getClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
