package com.javapro.formation.dto;

/** Ligne de requête du tableau de bord : une formation et le nombre de fois où elle a été demandée. */
public record FormationDemandeeDto(Long formationId, String intitule, Integer nbPlaces, Long nbDemandes,
                                   Long nbInscrits) {
}
