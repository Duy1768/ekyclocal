package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.LivenessService;
import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.presentation.response.LivenessResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/liveness")
@RequiredArgsConstructor
public class LivenessController {

    private final LivenessService livenessService;

    @GetMapping("/{sessionId}")
    public BaseResponse<LivenessResultResponse> getLivenessResult(
            @PathVariable String sessionId) {

        log.info(
                "step=controller_request_received operation=get_liveness_result sessionId={}",
                sessionId);

        LivenessResultResponse response =
                livenessService.getSessionResult(
                        sessionId);

        log.info(
                "step=controller_response_ready operation=get_liveness_result sessionId={} confidence={} livenessStatus={} ekycStatus={}",
                response.getSessionId(),
                response.getConfidence(),
                response.getLivenessStatus(),
                response.getEkycStatus());

        return BaseResponse.<LivenessResultResponse>builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .responseId(MDC.get(HeaderConstant.MDC_REQUEST_ID))
                .requestTime(LocalDateTime.now().toString())
                .data(response)
                .build();
    }
}
