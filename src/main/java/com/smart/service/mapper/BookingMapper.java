package com.smart.service.mapper;

import com.smart.service.dtoResponse.BookingResponse;
import com.smart.service.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "passenger.fullName", target = "passengerName")
    @Mapping(source = "passenger.contactNumber", target = "passengerPhone")
    @Mapping(source = "trip.driver.fullName", target = "trip.driverName")
    @Mapping(source = "trip.driver.id", target = "trip.driverId")
    BookingResponse toResponse(BookingEntity entity);
}