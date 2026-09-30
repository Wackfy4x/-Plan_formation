package com.javapro.formation.entity;

import com.javapro.formation.entity.Enum.RoleUtilisateur;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

@Getter
@Setter
@Entity
@Table(name = "utilisateur", uniqueConstraints = {
        @UniqueConstraint(name = "utilisateur_matricule_key",
                columnNames = {"matricule"}),
        @UniqueConstraint(name = "utilisateur_email_key",
                columnNames = {"email"})})
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "matricule", nullable = false, length = 20)
    private String matricule;

    @Size(max = 100)
    @NotNull
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Size(max = 100)
    @NotNull
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "role", columnDefinition = "role_utilisateur", nullable = false)
    private RoleUtilisateur role = RoleUtilisateur.COLLABORATEUR;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;


}