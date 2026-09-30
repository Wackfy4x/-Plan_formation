package com.javapro.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "formation")
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campagne_id", nullable = false)
    private Campagne campagne;

    @Size(max = 200)
    @NotNull
    @Column(name = "intitule", nullable = false, length = 200)
    private String intitule;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "nb_places", nullable = false)
    private Integer nbPlaces;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "est_validee", nullable = false)
    private Boolean estValidee;

    @Column(name = "date_validation")
    private Instant dateValidation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valide_par_id")
    private Utilisateur validePar;


}