package com.bank.ekyc.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivenessResultResponse {

    private String sessionId;

    private Float confidence;

    private String livenessStatus;

    private String ekycStatus;
}
