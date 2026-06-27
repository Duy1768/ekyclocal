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

    public String saveFile(
            MultipartFile file) {

        try {

            Path uploadDir =
                    Paths.get(
                            System.getProperty("user.dir"),
                            "uploads",
                            "idcard");
            log.info(
                    "step=create_directory path={}",
                    uploadDir.toAbsolutePath());

            Files.createDirectories(
                    uploadDir);

            log.info(
                    "step=create_directory path={}",
                    uploadDir.toAbsolutePath());

            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + file.getOriginalFilename();

            Path filePath =
                    uploadDir.resolve(
                            fileName);

            file.transferTo(
                    filePath.toFile());

            log.info(
                    "step=image_saved path={}",
                    filePath.toAbsolutePath());

            return filePath.toString();

        } catch (Exception ex) {

            log.error(
                    "step=image_save_failed",
                    ex);

            throw new RuntimeException(
                    "Cannot save image",
                    ex);
        }
    }
}