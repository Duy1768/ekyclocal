package com.bank.ekyc.infrastructure.external.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long transactionId;

    private BigDecimal amount;

    private LocalDateTime transactionTime;

    private String transferContent;

    private String status;

}
