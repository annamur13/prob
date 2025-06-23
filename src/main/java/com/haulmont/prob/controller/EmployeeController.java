package com.haulmont.prob.controller;

import com.haulmont.prob.model.Employee;
import com.haulmont.prob.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees") // Базовый путь для всех endpoint'ов
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

    // Создание нового сотрудника (с проверкой tezisId)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> request) {
        System.out.println("Received: " + request);

        // Проверка обязательных полей
        if (!request.containsKey("fullName")) {
            return ResponseEntity.badRequest().body("Missing fullName");
        }
        if (!request.containsKey("tezisId")) {
            return ResponseEntity.badRequest().body("Missing tezisId");
        }

        // Проверка уникальности tezisId
        if (repository.existsByTezisId(request.get("tezisId"))) {
            return ResponseEntity.badRequest().body("Employee with this tezisId already exists");
        }

        Employee employee = new Employee();
        employee.setFullName(request.get("fullName"));
        employee.setTezisId(request.get("tezisId"));

        Employee saved = repository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Получение сотрудника по tezisId
    @GetMapping("/by-tezis-id/{tezisId}")
    public ResponseEntity<Employee> getByTezisId(@PathVariable String tezisId) {
        return repository.findByTezisId(tezisId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}