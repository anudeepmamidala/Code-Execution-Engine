package com.example.codeforge.config;

import com.example.codeforge.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

   @Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.equals("/api/auth/login")
        || path.equals("/api/auth/register")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs");
}

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws java.io.IOException, jakarta.servlet.ServletException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.validateToken(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = jwtUtil.getUsername(token);
                String role = jwtUtil.getRole(token);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}
