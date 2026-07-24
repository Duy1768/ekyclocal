package com.bank.ekyc.infrastructure.external.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistoryResponse {

    private String deviceCode;

    private String hostname;

    private String osName;

    private String macAddress;

    private String apiName;

    private String status;

    private String responseCode;

    private Long durationMs;

    private LocalDateTime requestTime;

}
