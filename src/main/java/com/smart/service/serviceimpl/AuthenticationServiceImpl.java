package com.smart.service.serviceimpl;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.smart.service.entity.RoleEntity;
import com.smart.service.enums.enums;
import com.smart.service.exception.BadRequestException;
import com.smart.service.exception.ResourceNotFoundException;
import com.smart.service.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.service.dtoRequest.AuthenticationRequest;
import com.smart.service.dtoRequest.RegisterUserRequest;
import com.smart.service.dtoRequest.RoleAssignRequest;
import com.smart.service.dtoResponse.APIsResponse;
import com.smart.service.dtoResponse.AuthenticationResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.repository.RoleRepository;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.AuthenticationService;
import com.smart.service.service.JwtService;
import jakarta.transaction.Transactional;

// This is the service implement
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
        // Dependency injections
        private final UserRepository userRepository;
        private final JwtService jwtService;
        private final RoleRepository roleRepository;
        private final AuthenticationManager authenticationManager;
        private final PasswordEncoder passwordEncoder;

        public AuthenticationServiceImpl(UserRepository userRepository, JwtService jwtService,
                        RoleRepository roleRepository, AuthenticationManager authenticationManager,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.jwtService = jwtService;
                this.roleRepository = roleRepository;
                this.authenticationManager = authenticationManager;
                this.passwordEncoder = passwordEncoder;
        }

        @Transactional // This ensures the data is actually written to the DB

        public APIsResponse<UserEntity> register(RegisterUserRequest registerDto) {
                // 1. Check duplicate email
                if (userRepository.existsByEmail(registerDto.getEmail())) {
                        throw new BadRequestException("User with this email already exists");
                }

                // 2. Build user
                UserEntity user = UserEntity.builder()
                                .fullName(registerDto.getFullName())
                                .email(registerDto.getEmail())
                                .password(passwordEncoder.encode(registerDto.getPassword()))
                                .contactNumber(registerDto.getContactNumber())
                                .gender(registerDto.getGender())
                                .status("ACTIVE")
                                .roles(new HashSet<>())
                                .build();

                // 3. Assign DEFAULT role
                RoleEntity userRole = roleRepository.findByName(enums.USER);
                if (userRole != null)
                        user.getRoles().add(userRole);

                userRepository.save(user);

                var jwtToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefresh(user);

                return APIsResponse.<UserEntity>builder()
                                .message("User registered successfully")
                                .statusCode(HttpStatus.CREATED.value())
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public APIsResponse<UserEntity> assignRoleToUser(RoleAssignRequest request) {
                UserEntity user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                RoleEntity role = roleRepository.findById(request.getRoleId())
                                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

                if (user.getRoles().contains(role)) {
                        throw new BadRequestException("User already has this role assigned");
                }

                user.getRoles().add(role);
                userRepository.save(user);

                List<String> rolesList = user.getRoles().stream()
                                .map(r -> r.getName().name())
                                .collect(Collectors.toList());

                return APIsResponse.<UserEntity>builder()
                                .message("Role assigned successfully")
                                .statusCode(HttpStatus.OK.value())
                                .userId(user.getId())
                                .role(rolesList)
                                .accessToken(jwtService.generateToken(user))
                                .build();
        }

        public APIsResponse<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {

                try {
                        // Authenticate with Spring Security
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        authenticationRequest.getEmail(),
                                                        authenticationRequest.getPassword()));
                } catch (Exception ex) {
                        // If authentication fails, throw custom exception
                        throw new UnauthorizedException("Invalid email or password"); // 401
                }

                // Find user by email
                UserEntity user = userRepository.findByEmail(authenticationRequest.getEmail());
                if (user == null) {
                        throw new UnauthorizedException("Invalid email or password");
                }

                // Generate JWT tokens
                var jwtToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefresh(user);

                // Map roles to list of strings
                List<String> rolesList = user.getRoles().stream()
                                .map(r -> r.getName().name())
                                .collect(Collectors.toList());

                // Build AuthenticationResponse
                AuthenticationResponse response = AuthenticationResponse.builder()
                                .userId(user.getId())
                                .gender(user.getGender())
                                .contactNumber(user.getContactNumber())
                                .role(rolesList)
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();

                // Wrap in APIsResponse
                return APIsResponse.<AuthenticationResponse>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Login successfully")
                                .data(response)
                                .build();
        }

}