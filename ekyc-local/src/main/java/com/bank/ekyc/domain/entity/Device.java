package com.bank.ekyc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private Long id;

    private String deviceCode;

    private String hardwareUuid;

    private String hostname;

    private String macAddress;

    private String ipAddress;

    private String osName;

    private String osVersion;

    private String appVersion;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private LocalDateTime lastSeen;
}
