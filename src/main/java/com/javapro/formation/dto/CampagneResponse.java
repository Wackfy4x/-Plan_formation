package com.javapro.formation.dto;

import com.javapro.formation.entity.Campagne;

import java.time.LocalDate;

/**
 * @param phase PREPARATION (avant ouverture), OUVERTE (dépôt des demandes), VALIDATION_DRH
 *              (après clôture, avant publication) ou PUBLIEE.
 */
public record CampagneResponse(Long id, Integer annee, LocalDate dateEtablissement, LocalDate dateOuverture,
                               LocalDate dateCloture, LocalDate datePublication, String phase) {

    public static CampagneResponse from(Campagne c) {
        return new CampagneResponse(c.getId(), c.getAnnee(), c.getDateEtablissement(), c.getDateOuverture(),
                c.getDateCloture(), c.getDatePublication(), phase(c, LocalDate.now()));
    }

    static String phase(Campagne c, LocalDate today) {
        if (today.isBefore(c.getDateOuverture())) return "PREPARATION";
        if (!today.isAfter(c.getDateCloture())) return "OUVERTE";
        if (today.isBefore(c.getDatePublication())) return "VALIDATION_DRH";
        return "PUBLIEE";
    }
}
