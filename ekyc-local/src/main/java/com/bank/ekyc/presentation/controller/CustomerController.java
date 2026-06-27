package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.CustomerService;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.common.util.HmacUtil;
import com.bank.ekyc.config.SignatureProperties;
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
public class CustomerController {

    private final CustomerService customerService;

    private final SignatureProperties signatureProperties;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> createCustomer(

            @RequestParam String fullName,

            @RequestParam String idNumber,

            @RequestParam String phone,

            @RequestParam String email,

            @RequestParam MultipartFile idCardImage,

            @RequestHeader("Request-ID") String requestId,

            @RequestHeader("Request-DateTime") String requestDateTime,

            @RequestHeader("JWS-Signature") String signature) {

        log.info(
                "step=controller_request_received operation=create_customer");

        String body =
                "fullName=" + fullName
                        + "&idNumber=" + idNumber
                        + "&phone=" + phone
                        + "&email=" + email;

        String plainText =
                body +
                        requestId +
                        requestDateTime;

        String generatedSignature =
                HmacUtil.sign(
                        plainText,
                        signatureProperties.getSecretKey());

        log.info("body={}", body);
        log.info("plainText={}", plainText);
        log.info("generatedSignature={}", generatedSignature);
        log.info("receivedSignature={}", signature);

        if (!generatedSignature.equals(signature)) {

            return BaseResponse.<String>builder()
                    .responseCode(
                            ResponseCode.INVALID_SIGNATURE.getCode())
                    .responseMessage(
                            ResponseCode.INVALID_SIGNATURE.getMessage())
                    .responseId(requestId)
                    .requestTime(
                            LocalDateTime.now().toString())
                    .build();
        }

        BaseResponse<String> response =
                customerService.createCustomer(
                        fullName,
                        idNumber,
                        phone,
                        email,
                        idCardImage);

        log.info(
                "step=controller_response_ready operation=create_customer responseCode={}",
                response.getResponseCode());

        return response;
    }
}