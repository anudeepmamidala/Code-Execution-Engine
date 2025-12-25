package com.example.codeforge.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserMeResponse {
    private Long id;
    private String username;
    private String role;
}
