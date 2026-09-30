package com.javapro.formation.entity;

import com.javapro.formation.entity.Enum.StatutSouhait;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "souhait", indexes = {
        @Index(name = "souhait_demande_id_formation_id_idx",
                columnList = "demande_id, formation_id",
                unique = true),
        @Index(name = "souhait_demande_id_rang_idx",
                columnList = "demande_id, rang",
                unique = true)})
public class Souhait {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @NotNull
    @Column(name = "rang", nullable = false)
    private Short rang;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "statut", columnDefinition = "statut_souhait", nullable = false)
    private StatutSouhait statut = StatutSouhait.EN_ATTENTE_VALIDATION;

    @Column(name = "date_decision")
    private Instant dateDecision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decide_par_id")
    private Utilisateur decidePar;
}