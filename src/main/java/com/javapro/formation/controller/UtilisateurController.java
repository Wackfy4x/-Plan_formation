package com.javapro.formation.controller;

import com.javapro.formation.dto.UtilisateurResponse;
import com.javapro.formation.dto.UtilisateurUpdateRequest;
import com.javapro.formation.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
class UtilisateurController {
    private final UtilisateurService utilisateurService;

    @GetMapping("")
    public String test() {
        return "API plan formation";
    }

    @GetMapping("/utilisateur")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public List<UtilisateurResponse> lister(@RequestParam(required = false) String q) {
        return utilisateurService.lister(q);
    }

    @GetMapping("/utilisateur/{id}")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public UtilisateurResponse trouver(@PathVariable Long id) {
        return utilisateurService.trouver(id);
    }

    @PutMapping("/utilisateur/{id}")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public UtilisateurResponse mettreAJour(@PathVariable Long id, @Valid @RequestBody UtilisateurUpdateRequest req) {
        return utilisateurService.mettreAJour(id, req);
    }
}
