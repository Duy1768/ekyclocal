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
}