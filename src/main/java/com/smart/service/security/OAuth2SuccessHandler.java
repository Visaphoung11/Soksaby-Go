package com.smart.service.security;

import com.smart.service.entity.RoleEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.enums.enums;
import com.smart.service.repository.RoleRepository;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        Optional<UserEntity> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

        UserEntity user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {

            RoleEntity userRole = roleRepository.findByName(enums.USER);

            user = UserEntity.builder()
                    .fullName(name)
                    .email(email)
                    .status("ACTIVE")
                    .build();

            user.addRole(userRole);

            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);

        response.sendRedirect(
                "http://localhost:5500/oauth-success.html?token=" + token
        );
    }
}