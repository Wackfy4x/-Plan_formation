package com.javapro.formation.entity;

import com.javapro.formation.entity.Enum.RoleUtilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
public class Utilisateur implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    private String matricule;

    @Column(length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(unique = true)
    private String email;

    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private RoleUtilisateur role = RoleUtilisateur.COLLABORATEUR;

    @ManyToOne(optional = false)
    private Services service;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public @Nullable String getPassword() {
        return this.motDePasse;
    }

    @Override
    public String getUsername() {
        return this.matricule;
    }
}