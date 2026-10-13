package com.javapro.formation.dto;

import com.javapro.formation.entity.Campagne;
import com.javapro.formation.entity.Demande;
import com.javapro.formation.entity.Souhait;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record DemandeResponse(Long id, Long campagneId, Integer annee, Instant dateCreation,
                              Instant dateDerniereModif, boolean modifiable, List<SouhaitResponse> souhaits) {

    public static DemandeResponse from(Demande d, List<Souhait> souhaits, LocalDate today) {
        Campagne c = d.getCampagne();
        boolean modifiable = !today.isBefore(c.getDateOuverture()) && !today.isAfter(c.getDateCloture());
        boolean publie = !today.isBefore(c.getDatePublication());
        return new DemandeResponse(d.getId(), c.getId(), c.getAnnee(), d.getDateCreation(),
                d.getDateDerniereModif(), modifiable,
                souhaits.stream().map(s -> SouhaitResponse.from(s, publie)).toList());
    }
}
