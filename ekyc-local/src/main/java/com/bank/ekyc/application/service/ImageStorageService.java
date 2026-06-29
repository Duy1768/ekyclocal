package com.bank.ekyc.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {

    private static final String ID_CARD_FOLDER = "idcard";
    private static final String SELFIE_FOLDER = "selfie";

    public String saveIdCard(
            MultipartFile file) {

        return saveFile(
                file,
                ID_CARD_FOLDER);
    }

    public String saveSelfie(
            MultipartFile file) {

        return saveFile(
                file,
                SELFIE_FOLDER);
    }

    private String saveFile(
            MultipartFile file,
            String folderName) {

        try {

            Path uploadDir =
                    Paths.get(
                            System.getProperty("user.dir"),
                            "uploads",
                            folderName);

            Files.createDirectories(uploadDir);

            log.info(
                    "step=create_directory folder={} path={}",
                    folderName,
                    uploadDir.toAbsolutePath());

            String fileName =
                    UUID.randomUUID() + "_"
                            + file.getOriginalFilename();

            Path filePath =
                    uploadDir.resolve(fileName);

            file.transferTo(filePath.toFile());

            log.info(
                    "step=image_saved folder={} path={}",
                    folderName,
                    filePath.toAbsolutePath());

            return filePath.toString();

        } catch (Exception ex) {

            log.error(
                    "step=image_save_failed folder={}",
                    folderName,
                    ex);

            throw new RuntimeException(
                    "Cannot save image",
                    ex);
        }
    }
}
