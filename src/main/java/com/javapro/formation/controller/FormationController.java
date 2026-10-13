package com.javapro.formation.controller;

import com.javapro.formation.dto.CandidaturesResponse;
import com.javapro.formation.dto.FormationRequete;
import com.javapro.formation.dto.FormationResponse;
import com.javapro.formation.dto.ValidationRequete;
import com.javapro.formation.service.FormationService;
import com.javapro.formation.service.SouhaitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formation")
@RequiredArgsConstructor
public class FormationController {
    private final FormationService formationService;
    private final SouhaitService souhaitService;

    /** Catalogue de la campagne (courante par défaut), filtrable par mot-clé sur l'intitulé et la description. */
    @GetMapping
    public List<FormationResponse> lister(@AuthenticationPrincipal Jwt jwt,
                                          @RequestParam(required = false) Long campagneId,
                                          @RequestParam(required = false) String q) {
        return formationService.lister(jwt, campagneId, q);
    }

    @GetMapping("/{id}")
    public FormationResponse trouver(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return formationService.trouver(jwt, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public FormationResponse create(@Valid @RequestBody FormationRequete req) {
        return formationService.create(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public FormationResponse update(@PathVariable Long id, @Valid @RequestBody FormationRequete req) {
        return formationService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public void delete(@PathVariable Long id) {
        formationService.delete(id);
    }

    /** Personnes ayant demandé la formation, triées pour la validation, et personnes déjà inscrites. */
    @GetMapping("/{id}/candidatures")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public CandidaturesResponse candidatures(@PathVariable Long id) {
        return souhaitService.candidatures(id);
    }

    @PostMapping("/{id}/validation")
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public CandidaturesResponse valider(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                        @Valid @RequestBody ValidationRequete req) {
        return souhaitService.valider(jwt, id, req);
    }
}
