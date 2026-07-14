package com.bank.ekyc.presentation.response;

import lombok.Data;

import java.util.List;

@Data
public class LunarResponse {

    private String solarDate;

    private String lunarDate;

    private Integer day;

    private Integer month;

    private Integer year;

    private String canChi;

    private List<String> goodHours;

}