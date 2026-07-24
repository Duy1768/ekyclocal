package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.TransactionService;
import com.bank.ekyc.infrastructure.external.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/by-time")
    public List<TransactionResponse> findByTransactionTime(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime transactionTime) {

        return transactionService.findByTransactionTime(transactionTime);
    }
}
