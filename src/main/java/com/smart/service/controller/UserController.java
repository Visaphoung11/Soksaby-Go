package com.smart.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import com.smart.service.service.UserService;
import com.smart.service.dtoRequest.UserProfileUpdateRequest;
import com.smart.service.dtoResponse.UserProfileResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return ResponseEntity.ok(userService.getUserProfile(userDetails.getUsername()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@RequestBody UserProfileUpdateRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return ResponseEntity.ok(userService.updateUserProfile(userDetails.getUsername(), request));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}