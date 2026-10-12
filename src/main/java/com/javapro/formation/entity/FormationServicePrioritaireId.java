package com.javapro.formation.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class FormationServicePrioritaireId implements Serializable {
    private Long formationId;

    private Long serviceId;
}
