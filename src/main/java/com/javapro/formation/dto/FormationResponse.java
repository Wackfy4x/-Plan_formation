package com.javapro.formation.dto;

import com.javapro.formation.entity.Formation;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record FormationResponse(Long id, Long campagneId, Integer annee, String intitule, String description,
                                Integer nbPlaces, long nbInscrits, LocalDate dateFormation, boolean estValidee,
                                Instant dateValidation, String validePar,
                                List<ServiceResponse> servicesPrioritaires) {

    public static FormationResponse from(Formation f, long nbInscrits, List<ServiceResponse> servicesPrioritaires) {
        var v = f.getValidePar();
        return new FormationResponse(f.getId(), f.getCampagne().getId(), f.getCampagne().getAnnee(),
                f.getIntitule(), f.getDescription(), f.getNbPlaces(), nbInscrits, f.getDateFormation(),
                f.isEstValidee(), f.getDateValidation(), v == null ? null : v.getPrenom() + " " + v.getNom(),
                servicesPrioritaires);
    }
}
