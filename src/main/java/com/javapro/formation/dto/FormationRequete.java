package com.javapro.formation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record FormationRequete(
        @NotNull Long campagneId,
        @NotBlank @Size(max = 200) String intitule,
        String description,
        @NotNull @Positive Integer nbPlaces,
        LocalDate dateFormation,
        Set<Long> servicesPrioritairesIds
) {
}
