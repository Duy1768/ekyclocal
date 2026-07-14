package com.bank.ekyc.infrastructure.external.response;

import lombok.Data;

import java.util.List;

@Data
public class Lunar {

    private Integer day;

    private Integer month;

    private Integer year;

    private Boolean isLeap;

    private String holiday;

    private String canChiDay;

    private String canChiMonth;

    private String canChiYear;

    private String gioDauNgay;

    private String tietKhi;

    private List<TimeRange> gioHoangDao;

}