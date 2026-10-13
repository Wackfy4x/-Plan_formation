package com.javapro.formation.dto;

import com.javapro.formation.entity.Enum.RoleUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UtilisateurUpdateRequest(@NotBlank String nom,
                                       @NotBlank String prenom,
                                       @NotBlank @Email String email,
                                       @NotNull Long serviceId,
                                       @NotNull RoleUtilisateur role) {
}
