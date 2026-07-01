package com.bank.ekyc.application.service;

import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.exception.BusinessException;
import com.bank.ekyc.infrastructure.aws.AwsLivenessAdapter;
import com.bank.ekyc.presentation.response.LivenessResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.model.GetFaceLivenessSessionResultsResponse;
import software.amazon.awssdk.services.rekognition.model.LivenessSessionStatus;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LivenessService {

    private static final float APPROVAL_THRESHOLD = 90F;

    private final AwsLivenessAdapter awsLivenessAdapter;

    public String createSession() {

        return awsLivenessAdapter.createSession();
    }

    public LivenessResultResponse getSessionResult(
            String sessionId) {

        try {

            log.info(
                    "step=liveness_result_started sessionId={}",
                    sessionId);

            GetFaceLivenessSessionResultsResponse response =
                    awsLivenessAdapter.getSessionResults(
                            sessionId);

            log.info(
                    "aws_liveness_status={} sessionId={}",
                    response.statusAsString(),
                    sessionId);

            if (response.status() != LivenessSessionStatus.SUCCEEDED) {

                throw new BusinessException(
                        ResponseCode.LIVENESS_SESSION_NOT_READY);
            }

            Float confidence = response.confidence();

            if (confidence == null) {

                log.warn(
                        "step=liveness_result_missing_confidence sessionId={}",
                        sessionId);

                throw new BusinessException(
                        ResponseCode.LIVENESS_SESSION_NOT_READY);
            }

            boolean passed =
                    confidence >= APPROVAL_THRESHOLD;

            LivenessResultResponse result =
                    LivenessResultResponse.builder()
                            .sessionId(response.sessionId())
                            .confidence(confidence)
                            .livenessStatus(
                                    passed ? "PASS" : "FAIL")
                            .ekycStatus(
                                    passed ? "APPROVED" : "REJECTED")
                            .build();

            log.info(
                    "step=liveness_result_completed sessionId={} confidence={} livenessStatus={} ekycStatus={}",
                    result.getSessionId(),
                    result.getConfidence(),
                    result.getLivenessStatus(),
                    result.getEkycStatus());

            return result;

        } catch (RekognitionException ex) {

            String errorCode =
                    ex.awsErrorDetails() != null
                            ? ex.awsErrorDetails().errorCode()
                            : null;

            if ("ResourceNotFoundException".equals(errorCode)) {

                log.warn(
                        "step=liveness_result_session_not_found sessionId={}",
                        sessionId);

                throw new BusinessException(
                        ResponseCode.LIVENESS_SESSION_NOT_FOUND);
            }

            log.error(
                    "step=liveness_result_failed sessionId={}",
                    sessionId,
                    ex);
            throw ex;
        }
    }
}
