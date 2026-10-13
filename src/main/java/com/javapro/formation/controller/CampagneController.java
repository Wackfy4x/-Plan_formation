package com.javapro.formation.controller;

import com.javapro.formation.dto.CampagneRequete;
import com.javapro.formation.dto.CampagneResponse;
import com.javapro.formation.service.CampagneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campagne")
@RequiredArgsConstructor
public class CampagneController {
    private final CampagneService campagneService;

    /** Dates de la procédure, visibles par tous les utilisateurs connectés. */
    @GetMapping
    public List<CampagneResponse> lister() {
        return campagneService.lister();
    }

    @GetMapping("/courante")
    public CampagneResponse courante() {
        return campagneService.trouverCourante();
    }

    @GetMapping("/{id}")
    public CampagneResponse trouver(@PathVariable Long id) {
        return campagneService.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public CampagneResponse create(@Valid @RequestBody CampagneRequete req) {
        return campagneService.create(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public CampagneResponse update(@PathVariable Long id, @Valid @RequestBody CampagneRequete req) {
        return campagneService.update(id, req);
    }
}
