package com.bank.ekyc.application.service;

import com.bank.ekyc.common.constant.HeaderConstant;
import com.bank.ekyc.common.constant.ResponseCode;
import com.bank.ekyc.common.exception.BusinessException;
import com.bank.ekyc.common.util.ChecksumUtil;
import com.bank.ekyc.domain.entity.Customer;
import com.bank.ekyc.domain.enums.CompareStatus;
import com.bank.ekyc.infrastructure.dao.CustomerDao;
import com.bank.ekyc.infrastructure.dao.FaceCompareHistoryDao;
import com.bank.ekyc.presentation.response.FaceCompareResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class FaceCompareService {

    private final CustomerDao customerDao;

    private final FaceCompareHistoryDao faceCompareHistoryDao;

    private final RekognitionClient rekognitionClient;

    private final ImageStorageService imageStorageService;

    public FaceCompareResponse compareFace(
            String customerCode,
            MultipartFile selfieImage) {

        long startTime =
                System.currentTimeMillis();

        try {

            log.info(
                    "step=face_compare_started customerCode={}",
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

            if (!Files.exists(idCardPath)) {

                throw new RuntimeException(
                        "Id Card Image Not Found: "
                                + idCardPath);
            }

            byte[] idCardBytes =
                    Files.readAllBytes(
                            idCardPath);

            log.info(
                    "step=rekognition_compare_started customerCode={} imagePath={}",
                    customerCode,
                    customer.getIdCardImage());

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

            double similarity =
                    response.faceMatches()
                            .stream()
                            .findFirst()
                            .map(
                                    faceMatch ->
                                            (double) faceMatch.similarity())
                            .orElse(0D);

            CompareStatus compareStatus;

            if (similarity >= 95D) {

                compareStatus =
                        CompareStatus.MATCH;

            } else if (similarity >= 80D) {

                compareStatus =
                        CompareStatus.REVIEW;

            } else {

                compareStatus =
                        CompareStatus.NOT_MATCH;
            }

            faceCompareHistoryDao.insert(
                    customerCode,
                    selfieImagePath,
                    selfieChecksum,
                    similarity,
                    compareStatus.name());

            long durationMs =
                    System.currentTimeMillis()
                            - startTime;

            log.info(
                    "step=face_compare_completed requestId={} customerCode={} similarity={} compareStatus={} durationMs={}",
                    MDC.get(
                            HeaderConstant.MDC_REQUEST_ID),
                    customerCode,
                    similarity,
                    compareStatus,
                    durationMs);

            return FaceCompareResponse.builder()
                    .customerCode(
                            customerCode)
                    .similarity(
                            similarity)
                    .compareStatus(
                            compareStatus.name())
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
