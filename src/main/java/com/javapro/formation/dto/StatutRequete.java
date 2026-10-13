package com.javapro.formation.dto;

import com.javapro.formation.entity.Enum.StatutSouhait;
import jakarta.validation.constraints.NotNull;

public record StatutRequete(@NotNull StatutSouhait statut) {
}
