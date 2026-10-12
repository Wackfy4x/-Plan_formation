package com.javapro.formation.controller;

import com.javapro.formation.entity.Services;
import com.javapro.formation.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
class ServiceController {
    private final ServiceService serviceService;
    @PostMapping("/{nom}")
    public Services create_services(@PathVariable String nom) {
        return serviceService.create_service(nom);
    }
}
