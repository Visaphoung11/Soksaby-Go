package com.smart.service.seeder;

import com.smart.service.entity.RoleEntity;
import com.smart.service.enums.enums;
import com.smart.service.repository.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;



import java.util.*;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        // 1. Define all roles including AGENT
        enums[] roleNames = new enums[] { enums.ADMIN, enums.USER, enums.DRIVER };

        Map<enums, String> roleDescriptionMap = Map.of(
                enums.ADMIN, "Administrator role with full access",
                enums.USER, "Standard user role for browsing",
                enums.DRIVER, "Driver role for giving services"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            // 2. Use findByName (matching our earlier Role entity change)
            Optional<RoleEntity> optionalRole = Optional.ofNullable(roleRepository.findByName(roleName));
            if (optionalRole.isEmpty()) {
                RoleEntity roleToCreate = new RoleEntity();
                roleToCreate.setName(roleName); // Use setName()
                roleToCreate.setDescription(roleDescriptionMap.get(roleName));
                roleRepository.save(roleToCreate);
                System.out.println("Seeded role: " + roleName);
            } else {
                System.out.println("Role already exists: " + roleName);
            }
        });
    }
}