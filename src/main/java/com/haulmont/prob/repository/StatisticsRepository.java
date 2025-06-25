package com.haulmont.prob.repository;

import com.haulmont.prob.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    // Базовые методы CRUD уже предоставляются JpaRepository

    // === Методы для работы с сотрудниками ===

    /**
     * Найти всю статистику по ID сотрудника
     */
    List<Statistics> findByEmployeeId(Long employeeId);

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
    List<Statistics> findByTaskId(String taskId);

    /**
     * Найти статистику по ID задачи и сотрудника
     */
    List<Statistics> findByTaskIdAndEmployeeId(String taskId, Long employeeId);

    /**
     * Проверить существование статистики по ID задачи
     */
    boolean existsByTaskId(String taskId);

    // === Комбинированные методы ===

    /**
     * Найти статистику по вероятности (больше или равно)
     */
    List<Statistics> findByProbabilityGreaterThanEqual(Double probability);

    /**
     * Найти статистику по вероятности (диапазон)
     */
    List<Statistics> findByProbabilityBetween(Double minProbability, Double maxProbability);

    /**
     * Найти последнюю запись статистики по сотруднику
     */
    @Query("SELECT s FROM Statistics s WHERE s.employee.id = :employeeId ORDER BY s.createdDate DESC LIMIT 1")
    Optional<Statistics> findLatestByEmployeeId(@Param("employeeId") Long employeeId);
}