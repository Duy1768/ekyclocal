package com.bank.ekyc.domain.gateway;

import com.bank.ekyc.infrastructure.external.response.TransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionGateway {

    List<TransactionResponse> getTransactions(Long lastTransactionId);
    List<TransactionResponse> findByTransactionTime(LocalDateTime transactionTime);
}
