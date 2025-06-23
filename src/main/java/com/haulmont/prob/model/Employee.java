package com.haulmont.prob.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List; // Добавлен импорт List
import java.util.ArrayList; // Добавлен импорт ArrayList

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
    private String tezisId;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statistics> statistics = new ArrayList<>();

    public Employee() {}
}