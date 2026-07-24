package com.bank.ekyc.application.service;

import com.bank.ekyc.domain.gateway.TransactionGateway;
import com.bank.ekyc.infrastructure.external.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionGateway transactionGateway;

    private final RegexService regexService;

    private final FileService fileService;

    private Long lastTransactionId = 0L;

    public void syncTransactions() {

        LocalDateTime transactionTime = LocalDateTime.now().minusMinutes(10);

        List<TransactionResponse> transactions =
                transactionGateway.findByTransactionTime(transactionTime);

        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        for (TransactionResponse transaction : transactions) {

            if (transaction.getTransactionId() <= lastTransactionId) {
                continue;
            }

            if (regexService.isValid(transaction.getTransferContent())) {
                fileService.append(transaction);
            }

            lastTransactionId = transaction.getTransactionId();
        }
    }

    public List<TransactionResponse> findByTransactionTime(
            LocalDateTime transactionTime) {

        return transactionGateway.findByTransactionTime(transactionTime);
    }

}
