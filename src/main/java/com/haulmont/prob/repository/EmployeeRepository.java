package com.haulmont.prob.repository;

import com.haulmont.prob.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByTezisId(UUID tezisId);

    Optional<Employee> findByTezisId(UUID tezisId);
    // м-д возвращающий сотрудника по tezis_id из базы данных

    //создать метод создающий сотрудника
}