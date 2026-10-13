package com.javapro.formation.repository;

import com.javapro.formation.entity.Campagne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampagneRepository extends JpaRepository<Campagne, Long> {
}
