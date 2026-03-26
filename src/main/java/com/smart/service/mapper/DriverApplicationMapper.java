package com.smart.service.mapper;

import com.smart.service.dtoRequest.DriverApplicationRequest;
import com.smart.service.dtoResponse.DriverApplicationResponse;
import com.smart.service.entity.DriverApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverApplicationMapper {

    DriverApplicationEntity toEntity(DriverApplicationRequest request);
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userFullName", source = "user.fullName")
    DriverApplicationResponse toResponse(DriverApplicationEntity entity);

    @Mapping(target = "id", ignore = true) // Never change the ID
    @Mapping(target = "user", ignore = true) // Never change the User relationship
    @Mapping(target = "status", ignore = true) // We handle status manually in Service
    void updateEntityFromRequest(DriverApplicationRequest request, @MappingTarget DriverApplicationEntity entity);
    List<DriverApplicationResponse> toResponseList(List<DriverApplicationEntity> entities);
}
