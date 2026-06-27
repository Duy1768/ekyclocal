package com.bank.ekyc.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Customer {

    private Long id;

    private String customerCode;

    private String fullName;

    private String idNumber;

    private String idCardImage;

    private String phone;

    private String email;

    private LocalDateTime createdTime;

    private String imageChecksum;
}

