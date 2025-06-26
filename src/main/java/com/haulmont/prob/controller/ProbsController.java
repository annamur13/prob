package com.haulmont.prob.controller;

import com.haulmont.prob.model.Employee;
import com.haulmont.prob.model.Statistics;
import com.haulmont.prob.repository.EmployeeRepository;
import com.haulmont.prob.repository.tezis.TezisTaskCount;
import com.haulmont.prob.repository.tezis.TezisTaskProbability;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/probs")
public class ProbsController {

    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate thesisJdbcTemplate;
    private final TezisTaskCount tezisTaskCount;
    private final TezisTaskProbability probabilityCalculator;

    public ProbsController(
            EmployeeRepository employeeRepository,
            @Qualifier("thesisJdbcTemplate") JdbcTemplate thesisJdbcTemplate,
            TezisTaskCount tezisTaskCount,
            TezisTaskProbability probabilityCalculator) {
        this.employeeRepository = employeeRepository;
        this.thesisJdbcTemplate = thesisJdbcTemplate;
        this.tezisTaskCount = tezisTaskCount;
        this.probabilityCalculator = probabilityCalculator;
    }

    @PostMapping("/echo")
    public String echoEndpoint(@RequestBody String body) {
        log.debug("Echo endpoint called with body: {}", body);
        return "Echo: " + body;
    }

    @GetMapping("/probability")
    @Transactional
    public String getEmployeeProbability(@RequestParam UUID user_id) {
        log.info("Processing probability request for user_id: {}", user_id);

        return employeeRepository.findByTezisId(user_id)
                .map(Employee::getId)
                .map(String::valueOf)
                .orElseGet(() -> {
                    try {
                        String fullName = thesisJdbcTemplate.queryForObject(
                                "SELECT name FROM sec_user WHERE id = ?",
                                String.class,
                                user_id
                        );

                        if (fullName == null) {
                            throw new UserNotFoundException(user_id);
                        }

                        Employee newEmployee = new Employee();
                        newEmployee.setTezisId(user_id);
                        newEmployee.setFullName(fullName);

                        Employee saved = employeeRepository.saveAndFlush(newEmployee); // save + flush

                        log.info("New employee saved: {}", saved);
                        long taskCount = tezisTaskCount.getAssignedTaskCount(user_id);
                        return String.valueOf(taskCount);

                    } catch (DataAccessException e) {
                        log.error("Database access error", e);
                        throw new DatabaseAccessException("Failed to access database", e);
                    }
                });
    }

    @GetMapping("/task-count")
    public long getAssignedTaskCount(@RequestParam UUID user_id) {
        log.info("Fetching task count for user: {}", user_id);
        return tezisTaskCount.getAssignedTaskCount(user_id);
    }

    @GetMapping("/calculate-probability")
    @Transactional
    public ResponseEntity<?> calculateTaskProbability(@RequestParam UUID user_id) {
        try {
            log.info("Calculating task probability for user: {}", user_id);

            long taskCount = tezisTaskCount.getAssignedTaskCount(user_id);
            double probability = probabilityCalculator.calculateProbability(taskCount);

            Employee employee = employeeRepository.findByTezisId(user_id).orElseGet(() -> {
                String fullName = thesisJdbcTemplate.queryForObject(
                        "SELECT name FROM sec_user WHERE id = ?",
                        String.class,
                        user_id
                );

                if (fullName == null) {
                    throw new UserNotFoundException(user_id);
                }

                Employee e = new Employee();
                e.setTezisId(user_id);
                e.setFullName(fullName);
                // сохранить тут же
                Employee saved = employeeRepository.saveAndFlush(e);
                return e;
            });

            employee.setTaskProbability(probability);

            Statistics stat = new Statistics();
            stat.setProbability(probability);
            stat.setCreatedDate(LocalDate.now());
            stat.setTaskId("N/A");
            stat.setEmployee(employee);
            log.info("Saving stat with date: {}", stat.getCreatedDate());
            // Инициализируем список, если null
            if (employee.getStatistics() == null) {
                employee.setStatistics(new ArrayList<>());
            }

            employee.getStatistics().add(stat);

            // Сохраняем employee с cascade = ALL
            //Employee saved = employeeRepository.saveAndFlush(employee);
            log.info("After save: total statistics = {}", employee.getStatistics().size());
            for (Statistics s : employee.getStatistics()) {
                log.info("Stat: id={}, prob={}, date={}", s.getId(), s.getProbability(), s.getCreatedDate());
            }

            log.info("Employee saved: {}", employee.getId());

            return ResponseEntity.ok(probability);

        } catch (UserNotFoundException e) {
            log.error("User not found: {}", user_id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + user_id);
        } catch (DataAccessException e) {
            log.error("Database error", e);
            return ResponseEntity.internalServerError().body("Database error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // Exceptions
    private static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(UUID userId) {
            super("User with ID " + userId + " not found in any database");
        }
    }

    private static class DatabaseAccessException extends RuntimeException {
        public DatabaseAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
