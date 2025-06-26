package com.haulmont.prob.repository.tezis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;


@Component
public class TezisTaskCount {

    private final JdbcTemplate thesisJdbcTemplate;

    @Autowired
    public TezisTaskCount(@Qualifier("thesisJdbcTemplate") JdbcTemplate thesisJdbcTemplate) {
        this.thesisJdbcTemplate = thesisJdbcTemplate;
    }

    public long getAssignedTaskCount(UUID tezisId) {
        try {
            String sql = """
                SELECT COUNT(wf_card.id) 
                FROM wf_card
                JOIN tm_task ON wf_card.id = tm_task.card_id
                WHERE wf_card.card_type = 20
                  AND wf_card.state = ',Assigned,'
                  AND tm_task.executor_id = ?
                """;

            Long count = thesisJdbcTemplate.queryForObject(
                    sql,
                    Long.class,
                    tezisId
            );

            return count != null ? count : 0L;

        } catch (Exception e) {
            // Логируем ошибку и возвращаем 0
            System.err.println("Error fetching task count: " + e.getMessage());
            return 0L;
        }
    }
}