package com.javapro.formation.service;

import com.javapro.formation.dto.FormationRequete;
import com.javapro.formation.dto.FormationResponse;
import com.javapro.formation.dto.ServiceResponse;
import com.javapro.formation.entity.Campagne;
import com.javapro.formation.entity.Enum.StatutSouhait;
import com.javapro.formation.entity.Formation;
import com.javapro.formation.entity.FormationServicePrioritaire;
import com.javapro.formation.entity.Services;
import com.javapro.formation.repository.FormationRepository;
import com.javapro.formation.repository.FormationServicePrioritaireRepository;
import com.javapro.formation.repository.ServiceRepository;
import com.javapro.formation.repository.SouhaitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormationService {
    private final FormationRepository formations;
    private final FormationServicePrioritaireRepository prioritaires;
    private final ServiceRepository services;
    private final SouhaitRepository souhaits;
    private final CampagneService campagneService;
    private final UtilisateurService utilisateurService;

    /**
     * Catalogue d'une campagne (la campagne courante par défaut), avec recherche par mot-clé.
     * Un collaborateur ne voit rien avant la date d'ouverture.
     */
    @Transactional(readOnly = true)
    public List<FormationResponse> lister(Jwt jwt, Long campagneId, String q) {
        Campagne campagne = campagneId != null ? campagneService.get(campagneId) : campagneService.courante();
        if (!visiblePour(jwt, campagne)) {
            return List.of();
        }
        String motCle = q == null ? "" : q.trim();
        List<Formation> liste = motCle.isEmpty()
                ? formations.findByCampagneIdOrderByIntitule(campagne.getId())
                : formations.rechercher(campagne.getId(), motCle);
        return enrichir(liste);
    }

    @Transactional(readOnly = true)
    public FormationResponse trouver(Jwt jwt, Long id) {
        Formation f = get(id);
        if (!visiblePour(jwt, f.getCampagne())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation introuvable");
        }
        return enrichir(List.of(f)).get(0);
    }

    @Transactional
    public FormationResponse create(FormationRequete req) {
        Formation f = new Formation();
        f.setCampagne(campagneService.get(req.campagneId()));
        appliquer(f, req);
        formations.save(f);
        remplacerServicesPrioritaires(f, req.servicesPrioritairesIds());
        return enrichir(List.of(f)).get(0);
    }

    @Transactional
    public FormationResponse update(Long id, FormationRequete req) {
        Formation f = get(id);
        if (!f.getCampagne().getId().equals(req.campagneId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La campagne d'une formation ne peut pas être modifiée");
        }
        if (req.nbPlaces() < souhaits.countByFormationIdAndStatut(id, StatutSouhait.INSCRIT)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Le nombre de places est inférieur au nombre d'inscrits");
        }
        appliquer(f, req);
        remplacerServicesPrioritaires(f, req.servicesPrioritairesIds());
        return enrichir(List.of(f)).get(0);
    }

    /** Une formation déjà demandée ne peut plus être supprimée. */
    @Transactional
    public void delete(Long id) {
        Formation f = get(id);
        if (souhaits.existsByFormationId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Des collaborateurs ont déjà demandé cette formation");
        }
        prioritaires.supprimerParFormation(id);
        formations.delete(f);
    }

    private Formation get(Long id) {
        return formations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation introuvable"));
    }

    private boolean visiblePour(Jwt jwt, Campagne campagne) {
        return utilisateurService.estDrh(jwt) || !LocalDate.now().isBefore(campagne.getDateOuverture());
    }

    private void appliquer(Formation f, FormationRequete req) {
        f.setIntitule(req.intitule());
        f.setDescription(req.description());
        f.setNbPlaces(req.nbPlaces());
        f.setDateFormation(req.dateFormation());
    }

    private void remplacerServicesPrioritaires(Formation f, Set<Long> serviceIds) {
        Set<Long> ids = serviceIds == null ? Set.of() : serviceIds;
        List<Services> trouves = services.findAllById(ids);
        if (trouves.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service prioritaire inconnu");
        }
        prioritaires.supprimerParFormation(f.getId());
        for (Services s : trouves) {
            var p = new FormationServicePrioritaire();
            p.getId().setFormationId(f.getId());
            p.getId().setServiceId(s.getId());
            p.setFormation(f);
            p.setService(s);
            prioritaires.save(p);
        }
    }

    /** Ajoute à chaque formation ses services prioritaires et son nombre d'inscrits (2 requêtes pour toute la liste). */
    private List<FormationResponse> enrichir(List<Formation> liste) {
        if (liste.isEmpty()) {
            return List.of();
        }
        List<Long> ids = liste.stream().map(Formation::getId).toList();
        Map<Long, List<ServiceResponse>> servicesParFormation = prioritaires.findByFormationIds(ids).stream()
                .collect(Collectors.groupingBy(p -> p.getFormation().getId(),
                        Collectors.mapping(p -> new ServiceResponse(p.getService().getId(), p.getService().getNom()),
                                Collectors.toList())));
        Map<Long, Long> inscrits = souhaits.compterParFormation(ids, StatutSouhait.INSCRIT).stream()
                .collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue()));
        return liste.stream()
                .map(f -> FormationResponse.from(f, inscrits.getOrDefault(f.getId(), 0L),
                        servicesParFormation.getOrDefault(f.getId(), List.of())))
                .toList();
    }
}
