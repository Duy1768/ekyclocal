package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.HistoryService;
import com.bank.ekyc.infrastructure.external.response.HistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public List<HistoryResponse> findAll() {

        return historyService.findAll();

    }

}
