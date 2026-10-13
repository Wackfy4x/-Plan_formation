package com.javapro.formation.controller;

import com.javapro.formation.dto.TableauDeBordResponse;
import com.javapro.formation.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
class DashboardController {
    private final DashboardService dashboardService;

    /** Formations les plus demandées et taux de satisfaction par rang de préférence. */
    @GetMapping
    @PreAuthorize("hasRole('ASSISTANT_RH')")
    public TableauDeBordResponse tableauDeBord(@RequestParam(required = false) Long campagneId) {
        return dashboardService.tableauDeBord(campagneId);
    }
}
