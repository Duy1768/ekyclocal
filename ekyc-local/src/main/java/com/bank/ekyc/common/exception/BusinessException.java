package com.bank.ekyc.common.exception;

import com.bank.ekyc.common.constant.ResponseCode;

public class BusinessException
        extends RuntimeException {

    private final ResponseCode responseCode;

    public BusinessException(
            ResponseCode responseCode) {

        super(
                responseCode.getMessage());

        this.responseCode =
                responseCode;
    }

    public ResponseCode getResponseCode() {

        return responseCode;
    }
}