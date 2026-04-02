package com.smart.service.controller;


import com.smart.service.dtoRequest.AuthenticationRequest;
import com.smart.service.dtoRequest.RegisterUserRequest;
import com.smart.service.dtoRequest.RoleAssignRequest;
import com.smart.service.dtoResponse.APIsResponse;
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
    public ResponseEntity<APIsResponse<?>> register(@RequestBody RegisterUserRequest registerDto,
                                                    HttpServletResponse response)
    {
        // Check duplicate email → throw BadRequestException (400)
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("User with email already exists");
        }

        //  Call service normally, no try/catch needed
        APIsResponse<?> apiResponse = authenticationService.register(registerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse); // 201 Created
    }

    @PostMapping("/assign-role")
    public ResponseEntity<APIsResponse<?>> assignRole(@RequestBody RoleAssignRequest request) {
        //  service throws ResourceNotFoundException or BadRequestException as needed
        APIsResponse<?> response = authenticationService.assignRoleToUser(request);
        return ResponseEntity.ok(response); // 200 OK
    }


    @PostMapping("/authenticate")
    public ResponseEntity<APIsResponse<?>> authenticate(@RequestBody AuthenticationRequest request,
                                                        HttpServletResponse response)
    {
        // service will throw UnauthorizedException for invalid credentials
        APIsResponse<?> apiResponse = authenticationService.authenticate(request);
        return ResponseEntity.ok(apiResponse); // 200 OK
    }

    @GetMapping("/ws-token")
    public ResponseEntity<APIsResponse<?>> getWsToken(@AuthenticationPrincipal UserEntity user) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        APIsResponse<?> response = authenticationService.getWsToken(user);
        return ResponseEntity.ok(response);
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(false) // true in HTTPS prod
                .sameSite("Lax") // or None + secure=true for cross-site HTTPS
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
