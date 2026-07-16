package com.bank.ekyc.infrastructure.external.response;

import lombok.Data;

@Data
public class TodayData {

    private Solar solar;

    private Lunar lunar;

    private Astrology astrology;

    private Long jd;

}