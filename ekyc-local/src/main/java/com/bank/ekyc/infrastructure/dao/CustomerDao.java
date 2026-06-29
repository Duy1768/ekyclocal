package com.bank.ekyc.infrastructure.dao;

import com.bank.ekyc.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    public int insert(Customer customer) {

        try {

            log.info(
                    "step=database_insert_started entity=customer customerCode={}",
                    customer.getCustomerCode());

            String sql = """
                INSERT INTO customer
                (
                    customer_code,
                    full_name,
                    id_number,
                    idcard_image,
                    image_checksum,
                    phone,
                    email,
                    created_time
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?
                )
                """;

            int insertedRows =
                    jdbcTemplate.update(
                            sql,
                            customer.getCustomerCode(),
                            customer.getFullName(),
                            customer.getIdNumber(),
                            customer.getIdCardImage(),
                            customer.getImageChecksum(),
                            customer.getPhone(),
                            customer.getEmail(),
                            customer.getCreatedTime()
                    );

            log.info(
                    "step=database_insert_completed entity=customer customerCode={} affectedRows={}",
                    customer.getCustomerCode(),
                    insertedRows);

            return insertedRows;

        } catch (Exception ex) {

            log.error(
                    "step=database_insert_failed entity=customer customerCode={} error={}",
                    customer.getCustomerCode(),
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    public Customer findByCustomerCode(
            String customerCode) {

        try {

            log.info(
                    "step=database_query_started entity=customer customerCode={}",
                    customerCode);

            String sql = """
                SELECT
                    id,
                    customer_code,
                    full_name,
                    id_number,
                    idcard_image,
                    image_checksum,
                    phone,
                    email,
                    created_time
                FROM customer
                WHERE customer_code = ?
                """;

            Customer customer =
                    jdbcTemplate.queryForObject(
                            sql,
                            (rs, rowNum) -> {

                                Customer result =
                                        new Customer();

                                result.setId(
                                        rs.getLong("id"));

                                result.setCustomerCode(
                                        rs.getString("customer_code"));

                                result.setFullName(
                                        rs.getString("full_name"));

                                result.setIdNumber(
                                        rs.getString("id_number"));

                                result.setIdCardImage(
                                        rs.getString("idcard_image"));

                                result.setImageChecksum(
                                        rs.getString("image_checksum"));

                                result.setPhone(
                                        rs.getString("phone"));

                                result.setEmail(
                                        rs.getString("email"));

                                result.setCreatedTime(
                                        rs.getTimestamp("created_time")
                                                .toLocalDateTime());

                                return result;
                            },
                            customerCode);

            log.info(
                    "step=database_query_completed entity=customer customerCode={}",
                    customerCode);

            return customer;

        } catch (EmptyResultDataAccessException ex) {

            log.warn(
                    "step=database_query_not_found entity=customer customerCode={}",
                    customerCode);

            return null;

        } catch (Exception ex) {

            log.error(
                    "step=database_query_failed entity=customer customerCode={} error={}",
                    customerCode,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }

}
