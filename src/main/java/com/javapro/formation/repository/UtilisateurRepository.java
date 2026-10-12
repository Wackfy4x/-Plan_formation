package com.javapro.formation.repository;

import com.javapro.formation.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByMatricule(String matricule);
    boolean existsByEmail(String email);
    boolean existsByMatricule(String matricule);
}
