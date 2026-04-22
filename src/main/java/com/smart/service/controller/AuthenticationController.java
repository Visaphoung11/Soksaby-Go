package com.smart.service.controller;

import com.smart.service.dtoRequest.AuthenticationRequest;
import com.smart.service.dtoRequest.RegisterUserRequest;
import com.smart.service.dtoRequest.RoleAssignRequest;
import com.smart.service.dtoResponse.APIsResponse;
import com.smart.service.dtoResponse.AuthenticationResponse;
import com.smart.service.exception.BadRequestException;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.exception.UnauthorizedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth-service")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationController(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<APIsResponse<?>> register(
            @RequestBody RegisterUserRequest registerDto,
            HttpServletResponse response) {

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("User with email already exists");
        }

        APIsResponse<?> apiResponse = authenticationService.register(registerDto);

        String token = apiResponse.getAccessToken();
        if (token != null) {
            setJwtCookie(response, token);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/assign-role")
    public ResponseEntity<APIsResponse<?>> assignRole(@RequestBody RoleAssignRequest request) {
        APIsResponse<?> response = authenticationService.assignRoleToUser(request); // FIXED
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<APIsResponse<?>> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {

        APIsResponse<?> apiResponse = authenticationService.authenticate(request);

        AuthenticationResponse authData = (AuthenticationResponse) apiResponse.getData();
        String token = authData.getAccessToken();

        setJwtCookie(response, token);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/ws-token") // ADDED mapping
    public ResponseEntity<APIsResponse<?>> getWsToken(
            @AuthenticationPrincipal UserEntity user) {

        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        APIsResponse<?> response = authenticationService.getWsToken(user);
        return ResponseEntity.ok(response);
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}