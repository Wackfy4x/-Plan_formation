package com.javapro.formation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FormationServicePrioritaire {
    @EmbeddedId
    private FormationServicePrioritaireId id = new FormationServicePrioritaireId();

    @MapsId("formationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Formation formation;

    @MapsId("serviceId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Services service;
}
