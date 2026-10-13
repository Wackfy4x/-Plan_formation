package com.javapro.formation.service;

import com.javapro.formation.dto.UtilisateurResponse;
import com.javapro.formation.dto.UtilisateurUpdateRequest;
import com.javapro.formation.entity.Utilisateur;
import com.javapro.formation.repository.ServiceRepository;
import com.javapro.formation.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    private final UtilisateurRepository utilisateurs;
    private final ServiceRepository services;

    /** Le subject du JWT est le matricule (voir {@link Utilisateur#getUsername()}). */
    public Utilisateur courant(Jwt jwt) {
        return utilisateurs.findByMatricule(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));
    }

    /** Lit le rôle dans le jeton, sans accès à la base. */
    public boolean estDrh(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        return roles != null && roles.contains("ASSISTANT_RH");
    }

    @Transactional(readOnly = true)
    public List<UtilisateurResponse> lister(String q) {
        List<Utilisateur> liste = (q == null || q.isBlank())
                ? utilisateurs.findAll(Sort.by("nom", "prenom"))
                : utilisateurs.rechercher(q.trim());
        return liste.stream().map(UtilisateurResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UtilisateurResponse trouver(Long id) {
        return UtilisateurResponse.from(get(id));
    }

    @Transactional
    public UtilisateurResponse mettreAJour(Long id, UtilisateurUpdateRequest req) {
        Utilisateur u = get(id);
        String email = req.email().toLowerCase();
        if (utilisateurs.findByEmail(email).filter(autre -> !autre.getId().equals(id)).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        u.setNom(req.nom());
        u.setPrenom(req.prenom());
        u.setEmail(email);
        u.setRole(req.role());
        u.setService(services.findById(req.serviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service inconnu")));
        return UtilisateurResponse.from(u);
    }

    private Utilisateur get(Long id) {
        return utilisateurs.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }
}
