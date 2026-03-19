package com.smart.service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private String message;
    private boolean success;
    private T data;
    private String timestamp;
}
