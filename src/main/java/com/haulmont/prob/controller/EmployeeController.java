package com.haulmont.prob.controller;

import com.haulmont.prob.model.Employee;
import com.haulmont.prob.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // Получение всех сотрудников
    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        try {
            List<Employee> employees = repository.findAll();
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Создание нового сотрудника
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> request) {
        System.out.println("Received: " + request);
        if (!request.containsKey("fullName")) {
            return ResponseEntity.badRequest().body("Missing fullName");
        }

        Employee employee = new Employee();
        employee.setFullName(request.get("fullName"));
        Employee saved = repository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}