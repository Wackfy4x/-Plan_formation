package com.javapro.formation.entity;

import com.javapro.formation.entity.Enum.StatutSouhait;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"demande_id", "formation_id"}),
        @UniqueConstraint(columnNames = {"demande_id", "rang"})})
public class Souhait {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Demande demande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Formation formation;

    private Short rang;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private StatutSouhait statut = StatutSouhait.EN_ATTENTE_VALIDATION;

    private Instant dateDecision;

    @ManyToOne(fetch = FetchType.LAZY)
    private Utilisateur decidePar;
}
