package com.bank.ekyc.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceCompareResponse {

    private String customerCode;

    private Double similarity;

    private String compareStatus;

    private String ekycStatus;

    private String livenessSessionId;

    private String idCardImageBase64;

    private String selfieImageBase64;
}
