package com.javapro.formation.repository;

import com.javapro.formation.entity.FormationServicePrioritaire;
import com.javapro.formation.entity.FormationServicePrioritaireId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface FormationServicePrioritaireRepository
        extends JpaRepository<FormationServicePrioritaire, FormationServicePrioritaireId> {

    @Query("select p from FormationServicePrioritaire p join fetch p.service where p.formation.id in :formationIds")
    List<FormationServicePrioritaire> findByFormationIds(@Param("formationIds") Collection<Long> formationIds);

    @Modifying(flushAutomatically = true)
    @Query("delete from FormationServicePrioritaire p where p.formation.id = :formationId")
    void supprimerParFormation(@Param("formationId") Long formationId);
}
