package com.bank.ekyc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

@Configuration
public class RekognitionConfig {

    @Bean
    public RekognitionClient rekognitionClient() {

        return RekognitionClient.builder()
                .region(
                        Region.AP_SOUTHEAST_1)
                .build();
    }
}