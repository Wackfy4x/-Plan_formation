package com.javapro.formation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class FormationServicePrioritaireId implements Serializable {
    @Serial
    private static final long serialVersionUID = -6859731265564086152L;
    @NotNull
    @Column(name = "formation_id", nullable = false)
    private Long formationId;

    @NotNull
    @Column(name = "service_id", nullable = false)
    private Long serviceId;


}