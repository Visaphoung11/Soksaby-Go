package com.smart.service.controller;

import com.smart.service.dtoResponse.AdminStatsSummaryResponse;
import com.smart.service.dtoResponse.UserResponse;
import com.smart.service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats/summary")
    public ResponseEntity<AdminStatsSummaryResponse> getSummary() {
        return ResponseEntity.ok(adminService.getSummaryStats());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }
}