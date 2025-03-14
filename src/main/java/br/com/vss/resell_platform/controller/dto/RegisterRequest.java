package br.com.vss.resell_platform.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for receiving registration requests
 */
public record RegisterRequest(
        @NotBlank(message = "Username is required")
        String username,
        
        @NotBlank(message = "Password is required")
        String password,
        
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
} 