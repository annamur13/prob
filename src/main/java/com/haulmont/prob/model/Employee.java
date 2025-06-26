package com.haulmont.prob.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    @JsonProperty("fullName")
    private String fullName;

    @Column(name = "tezis_id", nullable = false, unique = true)
    @JsonProperty("tezisId")
    private UUID tezisId;

    @Column(name = "task_probability")  // Новое поле для вероятности
    private Double taskProbability;     // Double, так как вероятность может быть 0.75 и т. д.

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Statistics> statistics = new ArrayList<>();

    public Employee() {}


}