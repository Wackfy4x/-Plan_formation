package com.javapro.formation.dto;

import java.util.List;

/**
 * @param candidatures triées : services prioritaires d'abord, puis ordre de préférence
 * @param avertissements candidats inscrits au-delà du nombre habituel de formations par an (exceptions possibles)
 */
public record CandidaturesResponse(Long formationId, String intitule, Integer nbPlaces, long nbInscrits,
                                   boolean estValidee, List<CandidatureResponse> candidatures,
                                   List<String> avertissements) {
}
