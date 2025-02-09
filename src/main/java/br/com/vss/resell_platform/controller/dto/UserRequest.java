package br.com.vss.resell_platform.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Username cannot be blank.")
        @NotNull(message = "Username cannot be empty.")
        String username,
        @NotBlank(message = "Password cannot be blank.")
        @NotNull(message = "Password cannot be empty.")
        @Size(min = 6, max = 30, message = "Password length must be between 6 and 30")
        String password,
        @NotBlank(message = "Email cannot be blank.")
        @NotNull(message = "Email cannot be empty.")
        String email
) {
}
