package com.smart.service.security;
import jakarta.servlet.http.Cookie;
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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // 1. Check if user exists
        UserEntity user = userRepository.findByEmail(email);

        // 2. If user is null, we MUST create and SAVE them, then assign it back to 'user'
        if (user == null) {
            RoleEntity userRole = roleRepository.findByName(enums.USER);

            user = UserEntity.builder()
                    .fullName(name)
                    .email(email)
                    .status("ACTIVE")
                    .build();

            user.addRole(userRole);

            // CRITICAL: Re-assign the saved user so it is no longer null
            user = userRepository.save(user);
        }

        // 3. Generate Token (Now guaranteed that 'user' is not null)
        String token = jwtService.generateToken(user);

        // 4. Create the Cookie
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true); // Secure against XSS
        cookie.setSecure(false); // Set to true in production (HTTPS)
        cookie.setPath("/"); // Available for all backend paths
        cookie.setMaxAge(86400); // 1 day
        response.addCookie(cookie);

        // 5. Redirect to React Dashboard (or your local dev port)
        // If using React, this is usually http://localhost:5173/dashboard
        response.sendRedirect("http://localhost:5173/dashboard");    }
}