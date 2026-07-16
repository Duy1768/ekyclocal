package com.bank.ekyc.infrastructure.external.response;

import lombok.Data;

@Data
public class TodayResponse {

    private Boolean success;

    private TodayData data;

}