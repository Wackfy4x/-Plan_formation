package com.javapro.formation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CampagneRequete(
        @NotNull Integer annee,
        @NotNull LocalDate dateEtablissement,
        @NotNull LocalDate dateOuverture,
        @NotNull LocalDate dateCloture,
        @NotNull LocalDate datePublication
) {
}
