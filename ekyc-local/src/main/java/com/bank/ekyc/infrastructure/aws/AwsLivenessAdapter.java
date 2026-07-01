package com.bank.ekyc.infrastructure.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.CreateFaceLivenessSessionRequest;
import software.amazon.awssdk.services.rekognition.model.CreateFaceLivenessSessionResponse;
import software.amazon.awssdk.services.rekognition.model.GetFaceLivenessSessionResultsRequest;
import software.amazon.awssdk.services.rekognition.model.GetFaceLivenessSessionResultsResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AwsLivenessAdapter {

    private final RekognitionClient rekognitionClient;

    public String createSession() {

        try {

            log.info("step=aws_liveness_create_session_started");

            CreateFaceLivenessSessionResponse response =
                    rekognitionClient.createFaceLivenessSession(
                            CreateFaceLivenessSessionRequest.builder()
                                    .clientRequestToken(
                                            UUID.randomUUID()
                                                    .toString())
                                    .build());

            log.info(
                    "step=aws_liveness_create_session_completed sessionId={}",
                    response.sessionId());

            return response.sessionId();

        } catch (RekognitionException ex) {

            log.error("step=aws_liveness_create_session_failed", ex);
            throw ex;
        }
    }

    public GetFaceLivenessSessionResultsResponse getSessionResults(
            String sessionId) {

        try {

            log.info(
                    "step=aws_liveness_get_session_results_started sessionId={}",
                    sessionId);

            GetFaceLivenessSessionResultsResponse response =
                    rekognitionClient.getFaceLivenessSessionResults(
                            GetFaceLivenessSessionResultsRequest.builder()
                                    .sessionId(sessionId)
                                    .build());

            log.info(
                    "step=aws_liveness_get_session_results_completed sessionId={} status={} confidence={}",
                    response.sessionId(),
                    response.statusAsString(),
                    response.confidence());

            return response;

        } catch (RekognitionException ex) {

            log.error(
                    "step=aws_liveness_get_session_results_failed sessionId={}",
                    sessionId,
                    ex);
            throw ex;
        }
    }

}
