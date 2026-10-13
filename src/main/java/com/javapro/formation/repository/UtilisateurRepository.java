package com.javapro.formation.repository;

import com.javapro.formation.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByMatricule(String matricule);
    boolean existsByEmail(String email);
    boolean existsByMatricule(String matricule);

    @Query("""
            select u from Utilisateur u
            where lower(u.nom) like lower(concat('%', :q, '%'))
               or lower(u.prenom) like lower(concat('%', :q, '%'))
               or lower(u.email) like lower(concat('%', :q, '%'))
               or lower(u.matricule) like lower(concat('%', :q, '%'))
            order by u.nom, u.prenom""")
    List<Utilisateur> rechercher(@Param("q") String q);
}
