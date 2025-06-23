package com.haulmont.prob.repository;

import com.haulmont.prob.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByTezisId(String tezisId);
    Optional<Employee> findByTezisId(String tezisId);
}