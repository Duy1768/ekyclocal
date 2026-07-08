package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.CustomerService;
import com.bank.ekyc.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final DataSource dataSource;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> createCustomer(
            @RequestParam String fullName,
            @RequestParam String idNumber,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam MultipartFile idCardImage) {

        log.info("step=controller_request_received operation=create_customer");

        try (Connection connection = dataSource.getConnection()) {
            log.info("Hikari_Test: Connection acquired successfully. Executing dummy query...");

            connection.prepareStatement("SELECT 1").execute();

            log.info("Hikari_Test: Holding connection and sleeping for 10000ms...");
            Thread.sleep(10000);

            log.info("Hikari_Test: Sleep finished. Releasing connection...");
        } catch (Exception e) {
            log.error("Hikari_Test: Error during holding connection", e);
        }

        BaseResponse<String> response =
                customerService.createCustomer(
                        fullName, idNumber, phone, email, idCardImage);

        log.info("step=controller_response_ready operation=create_customer responseCode={}",
                response.getResponseCode());

        return response;
    }
}