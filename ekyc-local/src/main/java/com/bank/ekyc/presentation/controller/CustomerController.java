package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.CustomerService;
import com.bank.ekyc.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> createCustomer(

            @RequestParam String fullName,

            @RequestParam String idNumber,

            @RequestParam String phone,

            @RequestParam String email,

            @RequestParam MultipartFile idCardImage) {

        log.info(
                "step=controller_request_received operation=create_customer");

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