package com.haulmont.prob.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "statistics")
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "probability", nullable = false)
    private Double probability;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "task_id", length = 36)
    private String taskId;

    @ManyToOne
    @JoinColumn(name = "employee", nullable = false)
    @JsonIgnore
    private Employee employee;

    @Override
    public String toString() {
        return "Statistics{" +
                "id=" + id +
                ", probability=" + probability +
                '}';
    }
// Конструкторы остаются без изменений
}