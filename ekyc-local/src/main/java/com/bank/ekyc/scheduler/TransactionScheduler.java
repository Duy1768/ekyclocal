package com.bank.ekyc.scheduler;

import com.bank.ekyc.application.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionScheduler {

    private final TransactionService transactionService;

    @Scheduled(cron = "0 */10 * * * *")
    public void syncTransactions() {

        log.info("Scheduler running...");

        transactionService.syncTransactions();

    }

}
