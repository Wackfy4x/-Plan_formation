package com.javapro.formation.service;

import com.javapro.formation.entity.Campagne;
import com.javapro.formation.repository.CampagneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampagneService {
    private final CampagneRepository campagneRepository;

    public Campagne create(Campagne campagne) {
        return campagneRepository.save(campagne);
    }
}
