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
    // We explicitly define the check constraint here to include DRIVER
    @Column(nullable = false, unique = true, columnDefinition = "varchar(255) check (name in ('ADMIN', 'USER', 'DRIVER'))")
    private enums name;

    private String description;
}

