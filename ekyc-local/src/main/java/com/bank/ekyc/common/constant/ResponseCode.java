package com.bank.ekyc.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    SUCCESS("0000", "Success"),

    INVALID_SIGNATURE("1001", "Invalid Signature"),

    INVALID_REQUEST("1002", "Invalid Request"),

    MISSING_HEADER("1009", "Missing Header"),

    FACE_NOT_MATCH("1003", "Face Not Match"),

    LIVENESS_FAIL("1004", "Liveness Fail"),

    ID_NUMBER_ALREADY_EXISTS("1005", "ID Number Already Exists"),

    LIVENESS_SESSION_NOT_READY("1006", "Liveness Session Not Ready"),

    LIVENESS_SESSION_NOT_FOUND("1007", "Liveness Session Not Found"),

    AWS_REKOGNITION_ERROR("1008", "AWS Rekognition Error"),

    SYSTEM_ERROR("9999", "System Error"),

    CUSTOMER_NOT_FOUND("2001", "Customer Not Found"),

    SELFIE_IMAGE_DUPLICATE_ID_CARD_IMAGE("2002", "Selfie Image Duplicate Id Card Image");

    private final String code;

    private final String message;
}
