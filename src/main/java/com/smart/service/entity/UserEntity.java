package com.smart.service.entity;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



@Entity
@Table(name = "users") // Explicitly mapping to your 'user' table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserEntity implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "phone")
    private String contactNumber;

    @JsonIgnore
    private String password;
    @Column(name = "gender")
    private String gender;
    @Column(name = "profile_image")
    private String profileImage;
    private String status; // e.g., "ACTIVE", "INACTIVE"

    // Many-to-Many relationship with the 'role' table via 'user_role' join table
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();
    public void addRole(RoleEntity role) {
        this.roles.add(role);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream()
                .map(role ->{
                    String roleString = role.getName().name().toUpperCase();
                    return new SimpleGrantedAuthority("ROLE_" + roleString);
                })
                .collect(Collectors.toList());

    }



    @Override
    public String getUsername() {
        return this.email;
    }
}
