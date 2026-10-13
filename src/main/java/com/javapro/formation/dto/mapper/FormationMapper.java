package com.javapro.formation.dto.mapper;

import com.javapro.formation.dto.FormationRequete;
import com.javapro.formation.entity.Formation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FormationMapper {
    FormationRequete toDto(Formation formation);

    @Mapping( target = "estValidee", ignore = true)
    @Mapping(target = "dateValidation", ignore = true)
    @Mapping(target = "validePar", ignore = true)
    Formation toEntity(FormationRequete formationRequete);
}
