package com.bank.ekyc.infrastructure.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FaceCompareHistoryDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_FACE_COMPARE_HISTORY_SQL = """
        INSERT INTO face_compare_history
        (
            customer_code,
            selfie_image_path,
            selfie_checksum,
            similarity,
            compare_status,
            created_time
        )
        VALUES
        (
            ?, ?, ?, ?, ?, ?
        )
        """;

    public int insert(
            String customerCode,
            String selfieImagePath,
            String selfieChecksum,
            Double similarity,
            String compareStatus) {

        log.info(
                "step=database_insert_started table=face_compare_history customerCode={}",
                customerCode);

        int affectedRows = jdbcTemplate.update(
                INSERT_FACE_COMPARE_HISTORY_SQL,
                customerCode,
                selfieImagePath,
                selfieChecksum,
                similarity,
                compareStatus,
                LocalDateTime.now()
        );

        log.info(
                "step=database_insert_completed table=face_compare_history customerCode={} affectedRows={}",
                customerCode,
                affectedRows);

        return affectedRows;
    }

}