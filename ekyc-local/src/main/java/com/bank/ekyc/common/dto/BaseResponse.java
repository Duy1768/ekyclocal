package com.bank.ekyc.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse<T> {

    private String responseCode;

    private String responseMessage;

    private String responseId;

    private String requestTime;

    private T data;
}