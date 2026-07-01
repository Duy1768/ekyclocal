package com.bank.ekyc.application.service;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.exception.BusinessException;
import com.bank.ekyc.common.util.ChecksumUtil;
import com.bank.ekyc.domain.entity.Customer;
import com.bank.ekyc.domain.enums.CompareStatus;
import com.bank.ekyc.infrastructure.aws.AwsRekognitionAdapter;
import com.bank.ekyc.infrastructure.dao.CustomerDao;
import com.bank.ekyc.infrastructure.dao.FaceCompareHistoryDao;
import com.bank.ekyc.presentation.response.FaceCompareResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class FaceCompareService {

    private static final double MATCH_THRESHOLD = 95D;

    private final CustomerDao customerDao;

    private final FaceCompareHistoryDao faceCompareHistoryDao;

    private final AwsRekognitionAdapter awsRekognitionAdapter;

    private final ImageStorageService imageStorageService;

    private final LivenessService livenessService;

    public FaceCompareResponse compareFace(
            String customerCode,
            MultipartFile selfieImage) {

        long startTime = System.currentTimeMillis();

        try {

            log.info(
                    "step=face_compare_started requestId={} customerCode={}",
                    MDC.get(HeaderConstant.MDC_REQUEST_ID),
                    customerCode);

            Customer customer =
                    customerDao.findByCustomerCode(
                            customerCode);

            if (customer == null) {

                throw new BusinessException(
                        ResponseCode.CUSTOMER_NOT_FOUND);
            }

            byte[] selfieBytes =
                    selfieImage.getBytes();

            String selfieChecksum =
                    ChecksumUtil.sha256(
                            selfieBytes);

            if (selfieChecksum.equals(
                    customer.getImageChecksum())) {

                throw new BusinessException(
                        ResponseCode.SELFIE_IMAGE_DUPLICATE_ID_CARD_IMAGE);
            }

            String selfieImagePath =
                    imageStorageService.saveSelfie(
                            selfieImage);

            Path idCardPath =
                    Path.of(
                            customer.getIdCardImage());

            if (!Files.exists(
                    idCardPath)) {

                throw new BusinessException(
                        ResponseCode.SYSTEM_ERROR);
            }

            byte[] idCardBytes =
                    Files.readAllBytes(
                            idCardPath);

            String idCardImageBase64 =
                    Base64.getEncoder()
                            .encodeToString(
                                    idCardBytes);

            String selfieImageBase64 =
                    Base64.getEncoder()
                            .encodeToString(
                                    selfieBytes);

            log.info(
                    "step=compare_faces_call_aws customerCode={}",
                    customerCode);

            double similarity =
                    awsRekognitionAdapter.compareFaces(
                            idCardBytes,
                            selfieBytes);

            CompareStatus compareStatus =
                    similarity >= MATCH_THRESHOLD
                            ? CompareStatus.MATCH
                            : CompareStatus.NOT_MATCH;

            String ekycStatus;
            String livenessSessionId = null;

            if (compareStatus == CompareStatus.MATCH) {

                livenessSessionId =
                        livenessService.createSession();
                ekycStatus = "PENDING_LIVENESS";
            } else {
                ekycStatus = "REJECTED";
            }

            faceCompareHistoryDao.insert(
                    customerCode,
                    selfieImagePath,
                    selfieChecksum,
                    similarity,
                    compareStatus.name());

            long durationMs = System.currentTimeMillis() - startTime;

            log.info(
                    "step=face_compare_completed requestId={} customerCode={} similarity={} compareStatus={} ekycStatus={} livenessSessionId={} durationMs={}",
                    MDC.get(HeaderConstant.MDC_REQUEST_ID),
                    customerCode,
                    similarity,
                    compareStatus,
                    ekycStatus,
                    livenessSessionId,
                    durationMs);

            return FaceCompareResponse.builder()
                    .customerCode(customerCode)
                    .similarity(similarity)
                    .compareStatus(compareStatus.name())
                    .ekycStatus(ekycStatus)
                    .livenessSessionId(livenessSessionId)
                    .idCardImageBase64(idCardImageBase64)
                    .selfieImageBase64(selfieImageBase64)
                    .build();

        } catch (BusinessException ex) {

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "step=face_compare_failed customerCode={}",
                    customerCode,
                    ex);

            throw new RuntimeException(
                    "Face Compare Failed",
                    ex);
        }
    }
}
