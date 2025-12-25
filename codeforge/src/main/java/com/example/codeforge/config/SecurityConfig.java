package com.example.codeforge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ❌ No CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())

            // ✅ CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ✅ Stateless session (JWT = no session)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ Custom unauthorized response
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(authenticationEntryPoint)
            )

            // ✅ Authorization rules (POLICY ONLY)
            .authorizeHttpRequests(auth -> auth

                // PUBLIC AUTH ENDPOINTS
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register"
                ).permitAll()

                // SWAGGER / DOCS
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()

                // ADMIN ONLY
                .requestMatchers("/api/admin/**")
                .hasAuthority("ROLE_ADMIN")

                // EVERYTHING ELSE NEEDS JWT
                .anyRequest().authenticated()
            )

            // ✅ JWT filter runs BEFORE Spring auth
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔐 Password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Required for authentication infrastructure
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 🌍 CORS settings
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET","PUT","POST","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}