package com.javapro.formation.repository;

import com.javapro.formation.entity.Formation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByCampagneIdOrderByIntitule(Long campagneId);

    @Query("""
            select f from Formation f
            where f.campagne.id = :campagneId
              and (lower(f.intitule) like lower(concat('%', :q, '%'))
                   or lower(coalesce(f.description, '')) like lower(concat('%', :q, '%')))
            order by f.intitule""")
    List<Formation> rechercher(@Param("campagneId") Long campagneId, @Param("q") String q);

    /** Verrouille la formation : évite de dépasser la capacité si deux décisions arrivent en même temps. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Formation f where f.id = :id")
    Optional<Formation> findByIdForUpdate(@Param("id") Long id);
}
