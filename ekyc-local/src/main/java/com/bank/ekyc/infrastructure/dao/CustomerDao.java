package com.bank.ekyc.infrastructure.dao;

import com.bank.ekyc.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_CUSTOMER_SQL = """
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

    private static final String FIND_BY_CUSTOMER_CODE_SQL = """
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

    private static final RowMapper<Customer> CUSTOMER_ROW_MAPPER =
            (rs, rowNum) -> {

                Customer customer = new Customer();

                customer.setId(rs.getLong("id"));
                customer.setCustomerCode(rs.getString("customer_code"));
                customer.setFullName(rs.getString("full_name"));
                customer.setIdNumber(rs.getString("id_number"));
                customer.setIdCardImage(rs.getString("idcard_image"));
                customer.setImageChecksum(rs.getString("image_checksum"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));

                if (rs.getTimestamp("created_time") != null) {
                    customer.setCreatedTime(
                            rs.getTimestamp("created_time").toLocalDateTime());
                }

                return customer;
            };

    public int insert(Customer customer) {

        log.info(
                "step=database_insert_started table=customer customerCode={}",
                customer.getCustomerCode());

        int affectedRows = jdbcTemplate.update(
                INSERT_CUSTOMER_SQL,
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
                "step=database_insert_completed table=customer customerCode={} affectedRows={}",
                customer.getCustomerCode(),
                affectedRows);

        return affectedRows;
    }

    public Customer findByCustomerCode(String customerCode) {

        log.info(
                "step=database_query_started table=customer customerCode={}",
                customerCode);

        try {

            Customer customer = jdbcTemplate.queryForObject(
                    FIND_BY_CUSTOMER_CODE_SQL,
                    CUSTOMER_ROW_MAPPER,
                    customerCode);

            log.info(
                    "step=database_query_completed table=customer customerCode={}",
                    customerCode);

            return customer;

        } catch (EmptyResultDataAccessException ex) {

            log.warn(
                    "step=database_query_not_found table=customer customerCode={}",
                    customerCode);

            return null;
        }
    }

}