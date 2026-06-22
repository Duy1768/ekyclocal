package com.bank.ekyc.presentation.request;

import lombok.Data;

@Data
public class CustomerRequest {

    private String fullName;

    private String idNumber;

    private String phone;

    private String email;
}