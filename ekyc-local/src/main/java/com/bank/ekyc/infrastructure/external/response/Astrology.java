package com.bank.ekyc.infrastructure.external.response;

import lombok.Data;

import java.util.List;

@Data
public class Astrology {

    private List<String> conflictingAges;

    private String napAm;

    private String truc;

    private String sao;

    private LuckyDirections luckyDirections;

    private List<TimeRange> gioHacDao;

}