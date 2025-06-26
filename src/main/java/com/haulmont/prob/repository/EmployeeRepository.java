package com.haulmont.prob.repository;

import com.haulmont.prob.model.Employee;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByTezisId(UUID tezisId);
    @Qualifier
    Optional<Employee> findByTezisId(UUID tezisId);

}