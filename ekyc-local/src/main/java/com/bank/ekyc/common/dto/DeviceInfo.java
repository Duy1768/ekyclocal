package com.bank.ekyc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    private String hardwareUuid;

    private String hostname;

    private String macAddress;

    private String osName;

    private String osVersion;

    private String appVersion;

}
