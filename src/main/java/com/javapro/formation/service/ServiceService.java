package com.javapro.formation.service;

import com.javapro.formation.entity.Services;
import com.javapro.formation.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;

    @Transactional
    public Services create_service(String nom) {
        var exist = serviceRepository.existsByNom(nom);
        if (exist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        var service = new Services();
        service.setNom(nom);
        return serviceRepository.save(service);
    }
}
