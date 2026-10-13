package com.javapro.formation.service;

import com.javapro.formation.dto.FormationRequete;
import com.javapro.formation.dto.mapper.FormationMapper;
import com.javapro.formation.entity.Formation;
import com.javapro.formation.repository.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormationService {
    private final FormationRepository formationRepository;
    private final FormationMapper formationMapper;

    public FormationRequete create(FormationRequete formationRequete){
        Formation formation = formationMapper.toEntity(formationRequete);
        return formationMapper.toDto(formationRepository.save(formation));
    }
}
