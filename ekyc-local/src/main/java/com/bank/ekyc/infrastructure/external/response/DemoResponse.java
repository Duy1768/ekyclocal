package com.bank.ekyc.infrastructure.external.response;

import lombok.Data;

@Data
public class DemoResponse {

    private String responseCode;

    private String responseMessage;

    private Boolean retryable;

}