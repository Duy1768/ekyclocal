package com.bank.ekyc.infrastructure.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

@Component
@Slf4j
@RequiredArgsConstructor
public class AwsRekognitionAdapter {

    private final RekognitionClient rekognitionClient;

    public double compareFaces(
            byte[] idCardBytes,
            byte[] selfieBytes) {

        CompareFacesRequest request =
                CompareFacesRequest.builder()
                        .sourceImage(
                                Image.builder()
                                        .bytes(
                                                SdkBytes.fromByteArray(
                                                        idCardBytes))
                                        .build())
                        .targetImage(
                                Image.builder()
                                        .bytes(
                                                SdkBytes.fromByteArray(
                                                        selfieBytes))
                                        .build())
                        .similarityThreshold(
                                0F)
                        .build();

        CompareFacesResponse response =
                rekognitionClient.compareFaces(
                        request);

        return response.faceMatches()
                .stream()
                .findFirst()
                .map(
                        faceMatch ->
                                (double) faceMatch.similarity())
                .orElse(0D);
    }
}