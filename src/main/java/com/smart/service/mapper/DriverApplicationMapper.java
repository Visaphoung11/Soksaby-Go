package com.smart.service.mapper;

import com.smart.service.dtoRequest.DriverApplicationRequest;
import com.smart.service.dtoResponse.DriverApplicationResponse;
import com.smart.service.entity.DriverApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverApplicationMapper {

    DriverApplicationEntity toEntity(DriverApplicationRequest request);

    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userFullName", source = "user.fullName")
    DriverApplicationResponse toResponse(DriverApplicationEntity entity);

    List<DriverApplicationResponse> toResponseList(List<DriverApplicationEntity> entities);
}
