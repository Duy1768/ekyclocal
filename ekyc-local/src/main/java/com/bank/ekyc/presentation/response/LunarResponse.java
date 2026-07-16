package com.bank.ekyc.presentation.response;

import lombok.Data;

import java.util.List;

@Data
public class LunarResponse {

    private String code;

    private String message;

    private String solarDate;

    private String lunarDate;

    private Integer day;

    private Integer month;

    private Integer year;

    private String canChi;

    private List<String> goodHours;

}