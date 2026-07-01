package com.bank.ekyc.presentation.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LivenessSessionResponse {

    private String sessionId;
}