package com.bank.ekyc.application.service;

import com.bank.ekyc.infrastructure.external.response.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Service
public class FileService {

    private static final String FILE_NAME = "result.txt";

    public void append(TransactionResponse transaction) {

        File file = new File(FILE_NAME);
        log.info("Writing to: {}", file.getAbsolutePath());

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(file, true))) {

            writer.write(
                    transaction.getTransactionId()
                            + " | "
                            + transaction.getAmount()
                            + " | "
                            + transaction.getTransferContent());

            writer.newLine();

        } catch (IOException ex) {
            log.error("Write file failed", ex);
        }
    }

}
