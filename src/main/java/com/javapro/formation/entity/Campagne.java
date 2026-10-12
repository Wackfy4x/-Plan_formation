package com.javapro.formation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Campagne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer annee;

    private LocalDate dateEtablissement;

    private LocalDate dateOuverture;

    private LocalDate dateCloture;

    private LocalDate datePublication;
}
