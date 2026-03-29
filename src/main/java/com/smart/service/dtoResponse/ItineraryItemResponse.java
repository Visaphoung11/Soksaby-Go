package com.smart.service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItineraryItemResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
}
