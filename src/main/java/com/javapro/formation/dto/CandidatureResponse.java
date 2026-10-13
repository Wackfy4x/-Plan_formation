package com.javapro.formation.dto;

import com.javapro.formation.entity.Enum.StatutSouhait;

/**
 * Une candidature à une formation, telle que la voit la DRH.
 *
 * @param serviceEstPrioritaire le service du candidat figure parmi les services prioritaires de la formation
 * @param nbFormationsAccordees formations déjà accordées (statut inscrit) à ce candidat pour la campagne
 */
public record CandidatureResponse(Long souhaitId, Long utilisateurId, String nom, String prenom, String service,
                                  boolean serviceEstPrioritaire, Short rang, StatutSouhait statut,
                                  long nbFormationsAccordees) {
}
