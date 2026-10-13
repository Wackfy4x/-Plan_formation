package com.javapro.formation.dto;

import com.javapro.formation.entity.Campagne;
import com.javapro.formation.entity.Utilisateur;
import org.mapstruct.Mapper;

import java.time.Instant;

public record FormationRequete(
        Campagne campagne,
        String intitule,
        String description,
        Integer nbPlaces,
        Boolean estValidee,
        Instant dateValidation,
        Utilisateur validePar
) {
}
