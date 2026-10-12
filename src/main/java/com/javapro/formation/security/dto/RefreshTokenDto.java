package com.javapro.formation.security.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
        @NotBlank(message = "Le refresh token est requis")
        String refreshToken
) {
}
