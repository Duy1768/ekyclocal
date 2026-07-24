package com.bank.ekyc.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ThirdPartyCallHistory {

    private Long id;

    private Long deviceId;

    private String apiName;

    private LocalDateTime transactionTime;

    private LocalDateTime requestTime;

    private LocalDateTime responseTime;

    private Long durationMs;

    private String responseCode;

    private String status;

    private String errorMessage;

}
