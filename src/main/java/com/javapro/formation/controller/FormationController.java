package com.javapro.formation.controller;

import com.javapro.formation.dto.FormationRequete;
import com.javapro.formation.service.FormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/formation")
@RequiredArgsConstructor
public class FormationController {
    private final FormationService formationService;

    @PostMapping("")
    public FormationRequete create(@RequestBody FormationRequete formationRequete){
        return formationService.create(formationRequete);
    }
}
