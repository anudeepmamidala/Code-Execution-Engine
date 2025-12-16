package com.example.codeforge.config;

import com.example.codeforge.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extract JWT token from Authorization header
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;
            
            // Check if header exists and starts with "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);  // Remove "Bearer " prefix
                log.debug("JWT token found in Authorization header");
            }
            
            // Validate and process token
            if (token != null && jwtUtil.validateToken(token)) {
                username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                
                if (username != null) {
                    log.info("Setting user {} in SecurityContext", username);
                    
                    // Create authorities list with role
                    List<GrantedAuthority> authorities = 
                        Collections.singletonList(new SimpleGrantedAuthority(role));
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            authorities
                        );
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Set authentication in Spring Security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("User {} authenticated with role {}", username, role);
                }
            } else if (token != null) {
                log.warn("Invalid or expired JWT token");
            }
            
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
        }
        
        // Continue with filter chain
        filterChain.doFilter(request, response);
    }
}