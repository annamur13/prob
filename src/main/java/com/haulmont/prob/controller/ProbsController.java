package com.haulmont.prob.controller;

import com.haulmont.prob.model.Employee;
import com.haulmont.prob.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/probs")
public class ProbsController {

    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate thesisJdbcTemplate;

    public ProbsController(
            EmployeeRepository employeeRepository,
            @Qualifier("thesisJdbcTemplate") JdbcTemplate thesisJdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.thesisJdbcTemplate = thesisJdbcTemplate;
    }

    @PostMapping("/echo")
    public String test(@RequestBody String body) {
        log.info("Received echo request with body: {}", body);
        return "Echo: " + body;
    }

    @GetMapping("/probability")
    public String probability(@RequestParam UUID user_id) {
        log.info("Processing probability request for user_id: {}", user_id);

        return employeeRepository.findByTezisId(user_id)
                .map(employee -> {
                    log.debug("Employee found in local DB: {}", employee);
                    return String.valueOf(employee.getId());
                })
                .orElseGet(() -> createEmployeeFromThesisDb(user_id));
    }

    private String createEmployeeFromThesisDb(UUID user_id) {
        try {
            String fullName = thesisJdbcTemplate.queryForObject(
                    "SELECT name FROM sec_user WHERE id = ?",
                    String.class,
                    user_id
            );

            if (fullName == null) {
                log.error("User not found in thesis DB: {}", user_id);
                throw new UserNotFoundException(user_id);
            }

            Employee newEmployee = new Employee();
            newEmployee.setTezisId(user_id);
            newEmployee.setFullName(fullName);

            Employee savedEmployee = employeeRepository.save(newEmployee);
            log.info("Created new employee from thesis DB: {}", savedEmployee);

            return String.valueOf(savedEmployee.getId());

        } catch (Exception e) {
            log.error("Error while fetching user from thesis DB", e);
            throw new DataAccessException("Failed to access thesis database", e);
        }
    }

    // Custom exceptions
    private static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(UUID userId) {
            super("User with id " + userId + " not found in thesis database");
        }
    }

    private static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}