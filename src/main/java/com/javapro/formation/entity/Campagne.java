package com.javapro.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "campagne", uniqueConstraints = {@UniqueConstraint(name = "campagne_annee_key",
        columnNames = {"annee"})})
public class Campagne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "annee", nullable = false)
    private Integer annee;

    @NotNull
    @Column(name = "date_etablissement", nullable = false)
    private LocalDate dateEtablissement;

    @NotNull
    @Column(name = "date_ouverture", nullable = false)
    private LocalDate dateOuverture;

    @NotNull
    @Column(name = "date_cloture", nullable = false)
    private LocalDate dateCloture;

    @NotNull
    @Column(name = "date_publication", nullable = false)
    private LocalDate datePublication;


}