package com.bank.ekyc.application.service;

import com.bank.ekyc.domain.entity.ThirdPartyCallHistory;
import com.bank.ekyc.infrastructure.dao.ThirdPartyCallHistoryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThirdPartyCallHistoryService {

    private final ThirdPartyCallHistoryDAO historyDAO;

    public void save(ThirdPartyCallHistory history) {

        historyDAO.insert(history);

    }

}