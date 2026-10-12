package com.javapro.formation.repository;

import com.javapro.formation.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    public Boolean existsByNom(String nom);
}
