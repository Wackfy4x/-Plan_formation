package com.javapro.formation.service;

import com.javapro.formation.dto.ServiceResponse;
import com.javapro.formation.entity.Services;
import com.javapro.formation.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;

    @Transactional
    public Services create_service(String nom) {
        var exist = serviceRepository.existsByNom(nom);
        if (exist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce service existe déjà");
        }
        var service = new Services();
        service.setNom(nom);
        return serviceRepository.save(service);
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> lister() {
        return serviceRepository.findAll(Sort.by("nom")).stream()
                .map(s -> new ServiceResponse(s.getId(), s.getNom()))
                .toList();
    }
}
