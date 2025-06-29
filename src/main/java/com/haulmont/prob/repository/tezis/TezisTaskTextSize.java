package com.haulmont.prob.repository.tezis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class TezisTaskTextSize {

    private final JdbcTemplate thesisJdbcTemplate;

    @Autowired
    public TezisTaskTextSize(@Qualifier("thesisJdbcTemplate") JdbcTemplate thesisJdbcTemplate) {
        this.thesisJdbcTemplate = thesisJdbcTemplate;
    }

    public long getTextSizeCount(UUID cardId) {
        try {
            String sql = """
            SELECT LENGTH(full_descr) 
            FROM tm_task 
            WHERE card_id = ?
            """;

            Long length = thesisJdbcTemplate.queryForObject(
                    sql,
                    Long.class,
                    cardId
            );

            return length != null ? length : 0L;

        } catch (Exception e) {
            // Логируем ошибку и возвращаем 0
            System.err.println("Error fetching text length: " + e.getMessage());
            return 0L;
        }
    }
}