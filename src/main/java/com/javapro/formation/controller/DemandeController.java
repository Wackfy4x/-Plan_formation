package com.javapro.formation.controller;

import com.javapro.formation.dto.DemandeRequete;
import com.javapro.formation.dto.DemandeResponse;
import com.javapro.formation.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Demandes de l'utilisateur connecté. */
@RestController
@RequestMapping("/demande")
@RequiredArgsConstructor
class DemandeController {
    private final DemandeService demandeService;

    /** Années précédentes et demande en cours. */
    @GetMapping
    public List<DemandeResponse> mesDemandes(@AuthenticationPrincipal Jwt jwt) {
        return demandeService.mesDemandes(jwt);
    }

    @GetMapping("/campagne/{campagneId}")
    public DemandeResponse maDemande(@AuthenticationPrincipal Jwt jwt, @PathVariable Long campagneId) {
        return demandeService.maDemande(jwt, campagneId);
    }

    /** Crée ou remplace la demande : jusqu'à 5 formations, par ordre de préférence. */
    @PutMapping("/campagne/{campagneId}")
    public DemandeResponse enregistrer(@AuthenticationPrincipal Jwt jwt, @PathVariable Long campagneId,
                                       @Valid @RequestBody DemandeRequete req) {
        return demandeService.enregistrer(jwt, campagneId, req);
    }

    @DeleteMapping("/campagne/{campagneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@AuthenticationPrincipal Jwt jwt, @PathVariable Long campagneId) {
        demandeService.supprimer(jwt, campagneId);
    }
}
