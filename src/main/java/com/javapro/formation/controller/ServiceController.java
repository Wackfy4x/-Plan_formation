package com.javapro.formation.controller;

import com.javapro.formation.dto.ServiceResponse;
import com.javapro.formation.entity.Services;
import com.javapro.formation.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
class ServiceController {
    private final ServiceService serviceService;

    /** Public : le formulaire d'inscription a besoin de la liste des services. */
    @GetMapping
    public List<ServiceResponse> lister() {
        return serviceService.lister();
    }

    @PostMapping("/{nom}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public Services create_services(@PathVariable String nom) {
        return serviceService.create_service(nom);
    }
}
