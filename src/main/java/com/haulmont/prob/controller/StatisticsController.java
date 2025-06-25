package com.haulmont.prob.controller;

import com.haulmont.prob.model.Statistics;
import com.haulmont.prob.repository.StatisticsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsRepository statisticsRepository;

    public StatisticsController(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    /**
     * Получение всей статистики (с пагинацией)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Statistics>> getAllStatistics(Pageable pageable) {
        return ResponseEntity.ok(statisticsRepository.findAll(pageable));
    }

    /**
     * Получение статистики по ID сотрудника
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Statistics>> getStatisticsByEmployeeId(
            @PathVariable Long employeeId) {
        List<Statistics> statistics = statisticsRepository.findByEmployeeId(employeeId);
        return statistics.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(statistics);
    }

    /**
     * Получение статистики по tezisId сотрудника
     */
    @GetMapping("/employee/by-tezis-id/{tezisId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Statistics>> getStatisticsByEmployeeTezisId(
            @PathVariable String tezisId) {
        List<Statistics> statistics = statisticsRepository.findByEmployeeTezisId(tezisId);
        return statistics.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(statistics);
    }


    /**
     * Получение статистики по taskId
     */
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Statistics>> getStatisticsByTaskId(
            @PathVariable String taskId) {
        List<Statistics> statistics = statisticsRepository.findByTaskId(taskId);
        return statistics.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(statistics);
    }

    /**
     * Создание новой записи статистики
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Statistics> createStatistics(@RequestBody Statistics statistics) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(statisticsRepository.save(statistics));
    }

    /**
     * Обновление записи статистики
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Statistics> updateStatistics(
            @PathVariable Long id,
            @RequestBody Statistics statisticsDetails) {
        return statisticsRepository.findById(id)
                .map(existingStatistics -> {
                    existingStatistics.setProbability(statisticsDetails.getProbability());
                    existingStatistics.setCreatedDate(statisticsDetails.getCreatedDate());
                    existingStatistics.setTaskId(statisticsDetails.getTaskId());
                    return ResponseEntity.ok(statisticsRepository.save(existingStatistics));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаление записи статистики
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStatistics(@PathVariable Long id) {
        return statisticsRepository.findById(id)
                .map(statistics -> {
                    statisticsRepository.delete(statistics);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}