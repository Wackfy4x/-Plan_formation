package com.javapro.formation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Formations souhaitées, par ordre de préférence décroissant (la première a le rang 1). */
public record DemandeRequete(@NotNull @Size(max = 5) List<@NotNull Long> formationIds) {
}
