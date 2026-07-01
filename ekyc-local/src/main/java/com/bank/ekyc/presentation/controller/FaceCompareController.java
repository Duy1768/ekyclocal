package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.FaceCompareService;
import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.presentation.response.FaceCompareResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class FaceCompareController {

    private final FaceCompareService faceCompareService;

    @PostMapping(
            value = "/face-compare",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<FaceCompareResponse> faceCompare(

            @RequestParam String customerCode,

            @RequestParam MultipartFile selfieImage) {

        long startTime = System.currentTimeMillis();

        log.info(
                "step=controller_request_received operation=face_compare customerCode={}",
                customerCode);

        FaceCompareResponse data =
                faceCompareService.compareFace(
                        customerCode,
                        selfieImage);

        log.info(
                "step=controller_response_ready operation=face_compare customerCode={} similarity={} compareStatus={} durationMs={}",
                customerCode,
                data.getSimilarity(),
                data.getCompareStatus(),
                System.currentTimeMillis() - startTime);

        return BaseResponse.<FaceCompareResponse>builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .responseId(
                        MDC.get(HeaderConstant.MDC_REQUEST_ID))
                .requestTime(LocalDateTime.now().toString())
                .data(data)
                .build();
    }
}