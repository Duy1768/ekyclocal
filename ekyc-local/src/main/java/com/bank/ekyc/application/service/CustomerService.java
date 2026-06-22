package com.bank.ekyc.application.service;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.dto.BaseResponse;
import com.bank.ekyc.domain.entity.Customer;
import com.bank.ekyc.infrastructure.dao.CustomerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    private final ImageStorageService imageStorageService;

    public BaseResponse<String> createCustomer(
            String fullName,
            String idNumber,
            String phone,
            String email,
            MultipartFile idCardImage) {

        try {

            log.info(
                    "step=service_started operation=create_customer");

            Customer customer = new Customer();

            customer.setCustomerCode(
                    UUID.randomUUID().toString());

            String imagePath =
                    imageStorageService.saveFile(
                            idCardImage);

            customer.setFullName(
                    fullName);

            customer.setIdNumber(
                    idNumber);

            customer.setPhone(
                    phone);

            customer.setEmail(
                    email);

            customer.setIdCardImage(
                    imagePath);

            customer.setCreatedTime(
                    LocalDateTime.now());

            customerDao.insert(
                    customer);

            return BaseResponse.<String>builder()
                    .responseCode(
                            ResponseCode.SUCCESS.getCode())
                    .responseMessage(
                            ResponseCode.SUCCESS.getMessage())
                    .responseId(
                            MDC.get(
                                    HeaderConstant.MDC_REQUEST_ID))
                    .requestTime(
                            LocalDateTime.now().toString())
                    .data("Customer Created")
                    .build();

        } catch (DuplicateKeyException ex) {

            return BaseResponse.<String>builder()
                    .responseCode("1005")
                    .responseMessage(
                            "ID Number Already Exists")
                    .responseId(
                            MDC.get(
                                    HeaderConstant.MDC_REQUEST_ID))
                    .requestTime(
                            LocalDateTime.now().toString())
                    .build();

        } catch (Exception ex) {

            log.error(
                    "step=service_failed",
                    ex);

            return BaseResponse.<String>builder()
                    .responseCode(
                            ResponseCode.SYSTEM_ERROR.getCode())
                    .responseMessage(
                            ResponseCode.SYSTEM_ERROR.getMessage())
                    .responseId(
                            MDC.get(
                                    HeaderConstant.MDC_REQUEST_ID))
                    .requestTime(
                            LocalDateTime.now().toString())
                    .build();
        }
    }
}