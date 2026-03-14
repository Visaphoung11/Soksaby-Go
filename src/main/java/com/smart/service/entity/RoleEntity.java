package com.smart.service.entity;

import com.smart.service.enums.enums;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "roles")
@Data
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, columnDefinition = "TEXT") //
    private enums name;

    private String description;
}

