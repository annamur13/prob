package com.haulmont.prob.controller;

import com.haulmont.prob.model.Employee;
import com.haulmont.prob.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/probs")
public class ProbsController {

    private final EmployeeRepository employeeRepository;

    // Инжектим репозиторий через конструктор
    public ProbsController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/echo")
    public String test(@RequestBody String body) {
        return "Echo: " + body;
    }

    @GetMapping("/probability")
    public String probability(@RequestParam UUID user_id) {
        Optional<Employee> employee = employeeRepository.findByTezisId(user_id);
        Employee savedEmployee;

        if (employee.isEmpty()) {

            //если такого сотрудника нет, то мы идем в тезис в sec_user и запрашиваем значение колонки name по tezis_id

            Employee newEmployee = new Employee();
            newEmployee.setFullName("Маруся");
            newEmployee.setTezisId(user_id);
            savedEmployee = employeeRepository.save(newEmployee);
        } else {
            savedEmployee = employee.get(); // получаем существующего сотрудника
        }

        return String.valueOf(savedEmployee.getId());
    }


}
