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
        enums[] roleNames = { enums.ADMIN, enums.USER, enums.DRIVER };

        Map<enums, String> roleDescriptionMap = Map.of(
                enums.ADMIN,    "Administrator role with full access",
                enums.USER,     "Standard user role for browsing",
                enums.DRIVER,   "Driver role for giving services"
        );

        Arrays.stream(roleNames).forEach(roleEnum -> {
            // No .name() here — pass the enum directly
            RoleEntity existing = roleRepository.findByName(roleEnum);

            if (existing == null) {
                RoleEntity roleToCreate = new RoleEntity();
                roleToCreate.setName(roleEnum);                   // enum → correct
                roleToCreate.setDescription(roleDescriptionMap.get(roleEnum));
                roleRepository.save(roleToCreate);
                System.out.println("Seeded role: " + roleEnum.name());
            } else {
                System.out.println("Role already exists: " + roleEnum.name());
            }
        });
    }
}