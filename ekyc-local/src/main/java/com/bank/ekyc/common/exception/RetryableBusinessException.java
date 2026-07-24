package com.bank.ekyc.common.exception;

import lombok.Getter;

@Getter
public class RetryableBusinessException extends RuntimeException {

    private final String responseCode;

    public RetryableBusinessException(String responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}
