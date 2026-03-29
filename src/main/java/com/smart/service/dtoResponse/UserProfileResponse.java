package com.smart.service.dtoResponse;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String contactNumber;
    private String gender;
    private String profileImage;
    private List<String> roles;
}
