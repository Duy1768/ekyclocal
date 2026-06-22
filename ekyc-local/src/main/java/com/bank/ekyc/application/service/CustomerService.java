package com.bank.ekyc.application.service;

import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.domain.entity.Customer;
import com.bank.ekyc.infrastructure.dao.CustomerDao;
import com.bank.ekyc.presentation.request.CustomerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    public BaseResponse<String> createCustomer(
            CustomerRequest request) {

        log.info("step=service_started operation=create_customer");

        Customer customer = new Customer();

        customer.setCustomerCode(
                UUID.randomUUID().toString());

        log.info("step=customer_code_generated customerCode={}", customer.getCustomerCode());

        customer.setFullName(
                request.getFullName());

        customer.setIdNumber(
                request.getIdNumber());

        customer.setPhone(
                request.getPhone());

        customer.setEmail(
                request.getEmail());

        customer.setCreatedTime(
                LocalDateTime.now());

        log.info("step=customer_entity_mapped");

        log.info("step=customer_persistence_started customerCode={}", customer.getCustomerCode());
        customerDao.insert(customer);

        log.info("step=service_completed operation=create_customer customerCode={}", customer.getCustomerCode());

        return BaseResponse.<String>builder()
                .responseCode("0000")
                .responseMessage("Success")
                .responseId(MDC.get(HeaderConstant.MDC_REQUEST_ID))
                .requestTime(LocalDateTime.now().toString())
                .data("Customer Created")
                .build();
    }
}
