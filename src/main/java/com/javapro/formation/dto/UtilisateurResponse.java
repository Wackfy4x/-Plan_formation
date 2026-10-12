package com.javapro.formation.dto;

import com.javapro.formation.entity.Utilisateur;

public record UtilisateurResponse(Long id, String matricule, String nom, String prenom,
                                  String email, String role, String service) {

    public static UtilisateurResponse from(Utilisateur u) {
        return new UtilisateurResponse(u.getId(), u.getMatricule(), u.getNom(), u.getPrenom(),
                u.getEmail(), u.getRole().name(), u.getService().getNom());
    }

}