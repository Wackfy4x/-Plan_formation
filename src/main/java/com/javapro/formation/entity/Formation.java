package com.javapro.formation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Campagne campagne;

    @Column(length = 200)
    private String intitule;

    @Column(columnDefinition = "text")
    private String description;

    private Integer nbPlaces;

    private boolean estValidee;

    private Instant dateValidation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Utilisateur validePar;
}
