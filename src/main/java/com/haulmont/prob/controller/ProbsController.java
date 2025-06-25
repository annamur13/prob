package com.haulmont.prob.controller;

import com.haulmont.prob.model.Employee;
import com.haulmont.prob.repository.EmployeeRepository;
import com.haulmont.prob.repository.tezis.TezisTaskCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/probs")

public class ProbsController {

    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate thesisJdbcTemplate;
    private final TezisTaskCount tezisTaskCount;

    public ProbsController(
            EmployeeRepository employeeRepository,
            @Qualifier("thesisJdbcTemplate") JdbcTemplate thesisJdbcTemplate,
            TezisTaskCount tezisTaskCount) {
        this.employeeRepository = employeeRepository;
        this.thesisJdbcTemplate = thesisJdbcTemplate;
        this.tezisTaskCount = tezisTaskCount;
    }

    @PostMapping("/echo")
    public String echoEndpoint(@RequestBody String body) {
        log.debug("Echo endpoint called with body: {}", body);
        return "Echo: " + body;
    }

    @GetMapping("/probability")
    public String getEmployeeProbability(@RequestParam UUID user_id) {
        log.info("Processing probability request for user_id: {}", user_id);

        Optional<Employee> employeeOpt = employeeRepository.findByTezisId(user_id);

        if (employeeOpt.isPresent()) {
            return String.valueOf(employeeOpt.get().getId());
        }

        // Если сотрудника нет в основной БД, ищем в тезис БД
        try {
            String fullName = thesisJdbcTemplate.queryForObject(
                    "SELECT name FROM sec_user WHERE id = ?",
                    String.class,
                    user_id
            );

            if (fullName == null) {
                log.error("User not found in both databases: {}", user_id);
                throw new UserNotFoundException(user_id);
            }

            Employee newEmployee = new Employee();
            newEmployee.setTezisId(user_id);
            newEmployee.setFullName(fullName);

            Employee savedEmployee = employeeRepository.save(newEmployee);
            log.info("Created new employee from thesis DB: {}", savedEmployee);

            log.info("Fetching task count for user: {}", user_id);
            long taskCount = tezisTaskCount.getAssignedTaskCount(user_id);

            return String.valueOf(taskCount);

        } catch (Exception e) {
            log.error("Database access error", e);
            throw new DatabaseAccessException("Failed to access database", e);
        }
    }

    @GetMapping("/task-count")
    public long getAssignedTaskCount(@RequestParam UUID user_id) {
        log.info("Fetching task count for user: {}", user_id);
        return tezisTaskCount.getAssignedTaskCount(user_id);
    }

    // Exception classes
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