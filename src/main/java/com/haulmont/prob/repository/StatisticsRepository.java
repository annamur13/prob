package com.haulmont.prob.repository;

import com.haulmont.prob.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findByEmployeeId(Long employeeId);
}