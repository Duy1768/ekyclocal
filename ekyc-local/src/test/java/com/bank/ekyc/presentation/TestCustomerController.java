package com.bank.ekyc.presentation;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
public class TestCustomerController {

    @GetMapping("/api/v1/test/timeout")
    public BaseResponse<String> timeoutTest() {

        try {


            long start = System.currentTimeMillis();

            Thread.sleep(15000);

            long end = System.currentTimeMillis();

            log.info("Processing time : {} ms", end - start);


            return BaseResponse.<String>builder()
                    .responseCode(ResponseCode.SUCCESS.getCode())
                    .responseMessage(ResponseCode.SUCCESS.getMessage())
                    .responseId(MDC.get(HeaderConstant.MDC_REQUEST_ID))
                    .requestTime(LocalDateTime.now().toString())
                    .data("Completed after " + (end - start) + " ms")
                    .build();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return BaseResponse.<String>builder()
                    .responseCode(ResponseCode.SYSTEM_ERROR.getCode())
                    .responseMessage("Thread Interrupted")
                    .responseId(MDC.get(HeaderConstant.MDC_REQUEST_ID))
                    .requestTime(LocalDateTime.now().toString())
                    .build();
        }
    }
}