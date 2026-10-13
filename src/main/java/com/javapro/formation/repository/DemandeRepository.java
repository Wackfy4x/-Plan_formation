package com.javapro.formation.repository;

import com.javapro.formation.entity.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    @Query("select d from Demande d join fetch d.campagne c where d.utilisateur.id = :utilisateurId order by c.annee desc")
    List<Demande> findByUtilisateur(@Param("utilisateurId") Long utilisateurId);

    @Query("select d from Demande d join fetch d.campagne where d.utilisateur.id = :utilisateurId and d.campagne.id = :campagneId")
    Optional<Demande> findByUtilisateurAndCampagne(@Param("utilisateurId") Long utilisateurId,
                                                   @Param("campagneId") Long campagneId);
}
