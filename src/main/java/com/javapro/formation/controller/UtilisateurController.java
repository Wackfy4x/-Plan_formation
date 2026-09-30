package com.javapro.formation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("")
class UtilisateurController {

    @GetMapping("")
    public String test() {
        return "API plan formation";
    }
}
