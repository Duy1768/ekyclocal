package com.bank.ekyc.infrastructure.external.response;

import lombok.Data;

@Data
public class Solar {

    private Integer day;

    private Integer month;

    private Integer year;

    private String holiday;

    private String astronomy;

    private String dayOfWeek;

    private String zodiac;

}