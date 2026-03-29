package com.smart.service.mapper;

import com.smart.service.dtoRequest.UserProfileUpdateRequest;
import com.smart.service.dtoResponse.UserProfileResponse;
import com.smart.service.entity.RoleEntity;
import com.smart.service.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    UserProfileResponse toResponse(UserEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateUserFromDto(UserProfileUpdateRequest dto, @MappingTarget UserEntity entity);

    @Named("mapRoles")
    default List<String> mapRoles(Set<RoleEntity> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(role -> "ROLE_" + role.getName().name().toUpperCase())
                .collect(Collectors.toList());
    }
}
