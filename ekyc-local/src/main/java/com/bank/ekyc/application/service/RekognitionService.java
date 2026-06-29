package com.bank.ekyc.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

@Service
@RequiredArgsConstructor
public class RekognitionService {

    private final RekognitionClient rekognitionClient;

    public CompareFacesResponse compareFaces(
            byte[] idCardBytes,
            byte[] selfieBytes) {

        CompareFacesRequest request =
                CompareFacesRequest.builder()
                        .sourceImage(
                                Image.builder()
                                        .bytes(SdkBytes.fromByteArray(idCardBytes))
                                        .build())
                        .targetImage(
                                Image.builder()
                                        .bytes(SdkBytes.fromByteArray(selfieBytes))
                                        .build())
                        .similarityThreshold(0F)
                        .build();

        return rekognitionClient.compareFaces(request);
    }
}