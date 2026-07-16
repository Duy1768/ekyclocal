package com.bank.ekyc.domain.gateway;

import com.bank.ekyc.infrastructure.external.response.TodayResponse;

public interface LunarGateway {

    TodayResponse getToday();

}