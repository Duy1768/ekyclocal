package com.bank.ekyc.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    SUCCESS("0000", "Success"),

    INVALID_SIGNATURE("1001", "Invalid Signature"),

    INVALID_REQUEST("1002", "Invalid Request"),

    MISSING_HEADER("1002", "Missing Header"),

    FACE_NOT_MATCH("1003", "Face Not Match"),

    LIVENESS_FAIL("1004", "Liveness Fail"),

    SYSTEM_ERROR("9999", "System Error");

    private final String code;

    private final String message;
}