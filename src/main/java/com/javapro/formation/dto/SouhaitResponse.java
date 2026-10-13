package com.javapro.formation.dto;

import com.javapro.formation.entity.Enum.StatutSouhait;
import com.javapro.formation.entity.Souhait;

/** Tant que la campagne n'est pas publiée, le statut vu par le collaborateur reste « en attente de validation ». */
public record SouhaitResponse(Long id, Long formationId, String formation, Short rang, StatutSouhait statut) {

    public static SouhaitResponse from(Souhait s, boolean publie) {
        return new SouhaitResponse(s.getId(), s.getFormation().getId(), s.getFormation().getIntitule(),
                s.getRang(), publie ? s.getStatut() : StatutSouhait.EN_ATTENTE_VALIDATION);
    }
}
