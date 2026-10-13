package com.javapro.formation.repository;

import com.javapro.formation.entity.Campagne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampagneRepository extends JpaRepository<Campagne, Long> {
    boolean existsByAnnee(Integer annee);

    List<Campagne> findAllByOrderByAnneeDesc();

    /** Campagne la plus récente dont la préparation a commencé. */
    Optional<Campagne> findFirstByDateEtablissementLessThanEqualOrderByAnneeDesc(LocalDate date);
}
