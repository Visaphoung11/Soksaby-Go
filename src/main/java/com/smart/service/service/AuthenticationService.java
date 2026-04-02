package com.smart.service.service;

import com.smart.service.dtoRequest.AuthenticationRequest;
import com.smart.service.dtoRequest.RegisterUserRequest;
import com.smart.service.dtoRequest.RoleAssignRequest;
import com.smart.service.dtoResponse.APIsResponse;
import com.smart.service.dtoResponse.AuthenticationResponse;
import com.smart.service.entity.UserEntity;

public interface AuthenticationService {

    APIsResponse<UserEntity> register(RegisterUserRequest registerDto);

    APIsResponse<UserEntity> assignRoleToUser(RoleAssignRequest request);

    APIsResponse<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest);

    APIsResponse<AuthenticationResponse> getWsToken(UserEntity user);

}
