package com.smart.service.mapper;

import com.smart.service.dtoRequest.TripRequest; // Make sure this path is correct
import com.smart.service.dtoResponse.TripResponse;
import com.smart.service.entity.TripEntity;
import com.smart.service.entity.TripImageEntity;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TripMapper {

    // 1. Convert Request -> Entity (This fixes your Service error!)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", expression = "java(mapUrlsToEntities(request.imageUrls()))")
    TripEntity toEntity(TripRequest request);

    // 2. Convert Entity -> Response
    @Mapping(target = "images", expression = "java(mapEntitiesToUrls(trip.getImages()))")
    @Mapping(target = "driverName", source = "driver.fullName")
    @Mapping(target = "categoryName", source = "category.name")
    TripResponse toResponse(TripEntity trip);

    // 3. Update existing Entity (for PUT requests)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateEntityFromRequest(TripRequest request, @MappingTarget TripEntity entity);

    // --- HELPER METHODS ---

    default List<TripImageEntity> mapUrlsToEntities(List<String> urls) {
        if (urls == null) return new ArrayList<>();
        return urls.stream().map(url -> {
            TripImageEntity img = new TripImageEntity();
            img.setImageUrl(url);
            // We set the 'trip' back-reference in the Service, not here.
            return img;
        }).collect(Collectors.toList());
    }

    default List<String> mapEntitiesToUrls(List<TripImageEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(TripImageEntity::getImageUrl)
                .toList();
    }
}