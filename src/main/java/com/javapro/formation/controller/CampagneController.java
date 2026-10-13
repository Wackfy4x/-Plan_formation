package com.javapro.formation.controller;

import com.javapro.formation.entity.Campagne;
import com.javapro.formation.service.CampagneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/campagne")
@RequiredArgsConstructor
public class CampagneController {
    private final CampagneService campagneService;

    @PostMapping("")
    public Campagne create(@RequestBody Campagne campagne){
        return campagneService.create(campagne);
    }
}
