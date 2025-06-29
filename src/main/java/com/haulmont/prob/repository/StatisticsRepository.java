package com.haulmont.prob.repository;

import com.haulmont.prob.model.Statistics;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    @Qualifier
    // Базовые методы CRUD уже предоставляются JpaRepository

    // === Методы для работы с сотрудниками ===

    /**
     * Найти всю статистику по ID сотрудника
     */
    @Query("SELECT s FROM Statistics s INNER JOIN s.employee e WHERE e.id = :employee_id")
    List<Statistics> findByEmployee(@Param("employee_id") Long employee_id);

    /**
     * Проверить существование статистики по ID сотрудника
     */
    boolean existsByEmployeeId(Long employeeId);

    /**
     * Найти статистику по tezisId сотрудника
     */
    @Query("SELECT s FROM Statistics s JOIN s.employee e WHERE e.tezisId = :tezisId")
    List<Statistics> findByEmployeeTezisId(@Param("tezisId") String tezisId);

    /**
     * Количество записей статистики по сотруднику
     */
    @Query("SELECT COUNT(s) FROM Statistics s WHERE s.employee.id = :employeeId")
    long countByEmployeeId(@Param("employeeId") Long employeeId);

    // === Методы для фильтрации по дате ===


    /**
     * Найти статистику сотрудника за период (LocalDateTime)
     */
    @Query("SELECT s FROM Statistics s WHERE s.employee.id = :employeeId AND s.createdDate BETWEEN :startDate AND :endDate")
    List<Statistics> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // === Методы для работы с задачами ===

    /**
     * Найти статистику по ID задачи
     */
    List<Statistics> findByTaskId(String taskId); // Метод принимает String


}