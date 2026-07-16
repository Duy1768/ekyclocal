package com.bank.ekyc.application.service;

import com.bank.ekyc.domain.gateway.LunarGateway;
import com.bank.ekyc.infrastructure.external.response.TimeRange;
import com.bank.ekyc.infrastructure.external.response.TodayResponse;
import com.bank.ekyc.presentation.response.LunarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LunarService {

    private final LunarGateway gateway;

    public LunarResponse getToday() {

        TodayResponse thirdParty = gateway.getToday();

        LunarResponse response = new LunarResponse();

        response.setSolarDate(
                thirdParty.getData().getSolar().getDay() + "/"
                        + thirdParty.getData().getSolar().getMonth() + "/"
                        + thirdParty.getData().getSolar().getYear());

        response.setLunarDate(
                thirdParty.getData().getLunar().getDay() + "/"
                        + thirdParty.getData().getLunar().getMonth() + "/"
                        + thirdParty.getData().getLunar().getYear());

        response.setDay(
                thirdParty.getData().getLunar().getDay());

        response.setMonth(
                thirdParty.getData().getLunar().getMonth());

        response.setYear(
                thirdParty.getData().getLunar().getYear());

        response.setCanChi(
                thirdParty.getData().getLunar().getCanChiDay());

        List<String> goodHours = thirdParty.getData()
                .getLunar()
                .getGioHoangDao()
                .stream()
                .map(TimeRange::getName)
                .toList();

        response.setGoodHours(goodHours);

        return response;
    }
}