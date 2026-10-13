package com.javapro.formation.controller;

import com.javapro.formation.dto.CandidaturesResponse;
import com.javapro.formation.dto.InscriptionResponse;
import com.javapro.formation.dto.StatutRequete;
import com.javapro.formation.service.SouhaitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/souhait")
@RequiredArgsConstructor
class SouhaitController {
    private final SouhaitService souhaitService;

    /** Formations auxquelles je suis inscrit (campagnes publiées) ; aVenir=true : calendrier des prochaines. */
    @GetMapping("/inscriptions")
    public List<InscriptionResponse> mesInscriptions(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestParam(defaultValue = "false") boolean aVenir) {
        return souhaitService.mesInscriptions(jwt, aVenir);
    }

    /** DRH : désinscrire, inscrire un collaborateur mis en attente, ou remettre un souhait en attente. */
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public CandidaturesResponse changerStatut(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                              @Valid @RequestBody StatutRequete req) {
        return souhaitService.changerStatut(jwt, id, req);
    }
}
