package com.bank.ekyc.common.exception;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

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

        return buildErrorResponse(responseCode);
    }

    @ExceptionHandler(RekognitionException.class)
    public BaseResponse<Void> handleRekognitionException(
            RekognitionException exception) {

        log.error(
                "step=aws_rekognition_exception_handled errorCode={} errorMessage={}",
                exception.awsErrorDetails() != null
                        ? exception.awsErrorDetails().errorCode()
                        : "N/A",
                exception.getMessage(),
                exception);

        return buildErrorResponse(
                ResponseCode.AWS_REKOGNITION_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(
            Exception exception) {

        log.error(
                "step=system_exception_handled exceptionType={}",
                exception.getClass().getSimpleName(),
                exception);

        return buildErrorResponse(
                ResponseCode.SYSTEM_ERROR);
    }

    private BaseResponse<Void> buildErrorResponse(
            ResponseCode responseCode) {

        return BaseResponse.<Void>builder()
                .responseCode(responseCode.getCode())
                .responseMessage(responseCode.getMessage())
                .responseId(
                        MDC.get(HeaderConstant.MDC_REQUEST_ID))
                .requestTime(LocalDateTime.now().toString())
                .build();
    }
}
