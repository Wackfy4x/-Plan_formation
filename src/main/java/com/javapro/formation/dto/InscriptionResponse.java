package com.javapro.formation.dto;

import com.javapro.formation.entity.Formation;

import java.time.LocalDate;

public record InscriptionResponse(Long formationId, String intitule, String description, Integer annee,
                                  LocalDate dateFormation) {

    public static InscriptionResponse from(Formation f) {
        return new InscriptionResponse(f.getId(), f.getIntitule(), f.getDescription(),
                f.getCampagne().getAnnee(), f.getDateFormation());
    }
}
