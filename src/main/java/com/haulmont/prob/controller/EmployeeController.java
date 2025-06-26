package com.haulmont.prob.controller;

import com.haulmont.prob.model.Employee;
import com.haulmont.prob.model.Statistics;
import com.haulmont.prob.repository.EmployeeRepository;
import com.haulmont.prob.repository.StatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final StatisticsRepository statisticsRepository;

    public EmployeeController(EmployeeRepository employeeRepository,
                              StatisticsRepository statisticsRepository) {
        this.employeeRepository = employeeRepository;
        this.statisticsRepository = statisticsRepository;
    }

    /**
     * Получение всех сотрудников
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получение сотрудников, у которых есть статистика
     */
    @GetMapping("/with-statistics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Employee>> getEmployeesWithStatistics() {
        try {
            List<Employee> employees = employeeRepository.findAll().stream()
                    .filter(employee -> statisticsRepository.existsByEmployeeId(employee.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    /**
     * Получение статистики по конкретному сотруднику (по tezisId)
     * получение всей статистики по конкретному пользователю по tezis id employee потом по нему статистику всю и вернуть в формате джсон
     */
    @GetMapping("/{tezisId}/statistics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEmployeeStatistics(@PathVariable UUID tezisId) {
        log.info("REQUESTING!!!!!! statistics for tezisId: {}", tezisId);
        try {
            Optional<Employee> employee = employeeRepository.findByTezisId(tezisId);
            if (employee.isEmpty()) {
                log.warn("Employee not found for tezisId: {}", tezisId);
                return ResponseEntity.notFound().build();
            }
            log.info("Found employee: {}", employee.get().getId());

            List<Statistics> statistics = statisticsRepository.findByEmployeeId(employee.get().getId());
            log.info("Found {} statistics records", statistics.size());

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error retrieving statistics", e);
            return ResponseEntity.internalServerError().body("Error retrieving statistics");
        }
    }
}