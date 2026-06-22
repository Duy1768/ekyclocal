package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.CustomerService;
import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.presentation.request.CustomerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public BaseResponse<String> createCustomer(
            @RequestBody CustomerRequest request) {

        log.info("step=controller_request_received operation=create_customer");

        BaseResponse<String> response =
                customerService.createCustomer(request);

        log.info(
                "step=controller_response_ready operation=create_customer responseCode={}",
                response.getResponseCode());

        return response;
    }
}
