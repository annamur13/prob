package com.haulmont.prob.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "statistics")
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "probability", nullable = false)
    private Double probability;

    @Column(name = "createdDate", nullable = false)
    private LocalDate createdDate;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

}