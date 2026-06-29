package com.bank.ekyc.common.exception;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Void> handleBusinessException(
            BusinessException exception) {

        ResponseCode responseCode =
                exception.getResponseCode();

        log.warn(
                "step=business_exception_handled responseCode={} responseMessage={}",
                responseCode.getCode(),
                responseCode.getMessage());

        return BaseResponse.<Void>builder()
                .responseCode(responseCode.getCode())
                .responseMessage(responseCode.getMessage())
                .responseId(
                        MDC.get(HeaderConstant.MDC_REQUEST_ID))
                .requestTime(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(
            Exception exception) {

        log.error(
                "step=system_exception_handled exceptionType={}",
                exception.getClass().getSimpleName(),
                exception);

        return BaseResponse.<Void>builder()
                .responseCode(
                        ResponseCode.SYSTEM_ERROR.getCode())
                .responseMessage(
                        ResponseCode.SYSTEM_ERROR.getMessage())
                .responseId(
                        MDC.get(HeaderConstant.MDC_REQUEST_ID))
                .requestTime(LocalDateTime.now().toString())
                .build();
    }
}