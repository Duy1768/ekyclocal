package com.bank.ekyc.application.service;

import com.bank.ekyc.infrastructure.dao.HistoryDAO;
import com.bank.ekyc.infrastructure.external.response.HistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryDAO historyDAO;

    public List<HistoryResponse> findAll() {

        return historyDAO.findAll();

    }

}
