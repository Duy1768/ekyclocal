package com.bank.ekyc.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    SUCCESS("0000", "Success"),

    INVALID_SIGNATURE("1001", "Invalid Signature"),
    INVALID_REQUEST("1002", "Invalid Request"),
    MISSING_HEADER("1003", "Missing Header"),

    FACE_NOT_MATCH("2001", "Face Not Match"),
    LIVENESS_FAIL("2002", "Liveness Fail"),
    CUSTOMER_NOT_FOUND("2003", "Customer Not Found"),
    SELFIE_IMAGE_DUPLICATE_ID_CARD_IMAGE("2004", "Selfie Image Duplicate Id Card Image"),

    THIRD_PARTY_TIMEOUT("3001", "Third Party Timeout"),
    THIRD_PARTY_CONNECTION_ERROR("3002", "Third Party Connection Error"),
    THIRD_PARTY_CLIENT_ERROR("3003", "Third Party Client Error"),
    THIRD_PARTY_SERVER_ERROR("3004", "Third Party Server Error"),
    THIRD_PARTY_INVALID_RESPONSE("3005", "Invalid Third Party Response"),
    THIRD_PARTY_UNKNOWN_ERROR("3006", "Unknown Third Party Error"),

    SYSTEM_ERROR("9999", "System Error"),
    UNKNOWN_ERROR("9998", "Unknow Error");

    private final String code;

    private final String message;
}