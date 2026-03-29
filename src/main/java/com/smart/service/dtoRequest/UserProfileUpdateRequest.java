package com.smart.service.dtoRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    private String fullName;
    private String contactNumber;
    private String gender;
    private String profileImage;
}
