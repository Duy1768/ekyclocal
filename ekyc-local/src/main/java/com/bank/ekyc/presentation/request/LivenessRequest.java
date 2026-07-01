package com.bank.ekyc.presentation.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LivenessRequest {

    private String customerCode;

    private MultipartFile selfieImage;
}
