package com.haulmont.prob.controller;

import com.haulmont.prob.model.Statistics;
import com.haulmont.prob.repository.StatisticsRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsRepository repository;

    public StatisticsController(StatisticsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Statistics> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Statistics create(@RequestBody Statistics statistics) {
        return repository.save(statistics);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Statistics> getByEmployee(@PathVariable Long employeeId) {
        return repository.findByEmployeeId(employeeId);
    }
}