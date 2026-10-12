package com.javapro.formation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Size(max = 20) String matricule,
                              @NotBlank String nom,
                              @NotBlank String prenom,
                              @NotBlank @Email String email,
                              @NotBlank @Size(min = 8, max = 72) String motDePasse,   // BCrypt est limité à 72 octets
                              @NotNull Long serviceId) {
}
