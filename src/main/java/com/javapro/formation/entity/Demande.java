package com.javapro.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "demande", indexes = {@Index(name = "demande_utilisateur_id_campagne_id_idx",
        columnList = "utilisateur_id, campagne_id",
        unique = true)})
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campagne_id", nullable = false)
    private Campagne campagne;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "date_derniere_modif", nullable = false)
    private Instant dateDerniereModif;


}