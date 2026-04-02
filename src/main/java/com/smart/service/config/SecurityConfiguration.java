package com.smart.service.config;

import com.smart.service.filter.JwtAuthenticationFilter;
import com.smart.service.security.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                // CHANGE 1: Enable CORS with default settings (it will find your CorsConfig
                                // bean)
                                .cors(cors -> cors.configure(http))

                                .csrf(AbstractHttpConfigurer::disable)

                                .authorizeHttpRequests(request -> request
                                                .requestMatchers(
                                                                "/api/v1/auth-service/register",
                                                                "/api/v1/auth-service/authenticate",
                                                                "/api/v1/public/**",
                                                                "/oauth2/**",
                                                                "/login/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**",
                                                                "/swagger-resources/**",
                                                                "/webjars/**",
                                                                "/ws-soksabay/**",
                                                                "/api/v1/auth/**"

                                                ).permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/driver/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/trip/**").permitAll()
                                                .anyRequest().authenticated())

                                .oauth2Login(oauth -> oauth
                                                .successHandler(oAuth2SuccessHandler))

                                // CHANGE 2: Set to STATELESS if you want to strictly use your JWT Cookie
                                .sessionManagement(manager -> manager
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authenticationProvider(authenticationProvider)

                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(jwtAccessDeniedHandler))

                                // CHANGE 3: Add Logout to clear the cookie
                                .logout(logout -> logout
                                                .logoutUrl("/api/v1/auth/logout")
                                                .deleteCookies("jwt_token") // This tells the browser to delete your
                                                                            // cookie
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        response.setStatus(HttpServletResponse.SC_OK);
                                                }));

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration cfg = new CorsConfiguration();

                cfg.setAllowedOrigins(
                                List.of("http://localhost:5173", "http://localhost:5174", "http://localhost:5175")); // Your
                                                                                                                     // Vite
                                                                                                                     // URL
                cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                cfg.setAllowCredentials(true); // Must be true for cookies!
                cfg.setAllowedHeaders(List.of("*"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", cfg);
                return source;
        }
}