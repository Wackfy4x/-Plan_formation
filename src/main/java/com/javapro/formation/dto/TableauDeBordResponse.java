package com.javapro.formation.dto;

import java.util.List;

/**
 * @param formationsLesPlusDemandees formations triées par nombre de demandes décroissant
 * @param tauxSatisfaction pourcentage de souhaits satisfaits (statut inscrit) pour chaque rang de préférence
 * @param tauxSatisfactionGlobal pourcentage de souhaits satisfaits, tous rangs confondus
 */
public record TableauDeBordResponse(Long campagneId, Integer annee,
                                    List<FormationDemandeeDto> formationsLesPlusDemandees,
                                    List<TauxRang> tauxSatisfaction, double tauxSatisfactionGlobal) {

    public record TauxRang(int rang, long nbSouhaits, long nbSatisfaits, double pourcentage) {
    }
}
