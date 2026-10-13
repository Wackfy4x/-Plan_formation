package com.javapro.formation.service;

import com.javapro.formation.dto.DemandeRequete;
import com.javapro.formation.dto.DemandeResponse;
import com.javapro.formation.entity.Campagne;
import com.javapro.formation.entity.Demande;
import com.javapro.formation.entity.Formation;
import com.javapro.formation.entity.Souhait;
import com.javapro.formation.entity.Utilisateur;
import com.javapro.formation.repository.DemandeRepository;
import com.javapro.formation.repository.FormationRepository;
import com.javapro.formation.repository.SouhaitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeService {
    private final DemandeRepository demandes;
    private final SouhaitRepository souhaits;
    private final FormationRepository formations;
    private final CampagneService campagneService;
    private final UtilisateurService utilisateurService;

    /** Toutes les demandes du collaborateur : années précédentes et demande en cours. */
    @Transactional(readOnly = true)
    public List<DemandeResponse> mesDemandes(Jwt jwt) {
        Utilisateur u = utilisateurService.courant(jwt);
        List<Demande> liste = demandes.findByUtilisateur(u.getId());
        if (liste.isEmpty()) {
            return List.of();
        }
        Map<Long, List<Souhait>> souhaitsParDemande = souhaits
                .findByDemandeIds(liste.stream().map(Demande::getId).toList()).stream()
                .collect(Collectors.groupingBy(s -> s.getDemande().getId()));
        LocalDate today = LocalDate.now();
        return liste.stream()
                .map(d -> DemandeResponse.from(d, souhaitsParDemande.getOrDefault(d.getId(), List.of()), today))
                .toList();
    }

    @Transactional(readOnly = true)
    public DemandeResponse maDemande(Jwt jwt, Long campagneId) {
        Utilisateur u = utilisateurService.courant(jwt);
        Demande d = demandes.findByUtilisateurAndCampagne(u.getId(), campagneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune demande pour cette campagne"));
        return DemandeResponse.from(d, souhaits.findByDemandeIds(List.of(d.getId())), LocalDate.now());
    }

    /**
     * Crée ou remplace la demande du collaborateur pour la campagne. Les souhaits sont remplacés en bloc :
     * pendant la période de dépôt, aucune décision n'a encore été prise sur eux.
     */
    @Transactional
    public DemandeResponse enregistrer(Jwt jwt, Long campagneId, DemandeRequete req) {
        Utilisateur u = utilisateurService.courant(jwt);
        Campagne campagne = campagneService.get(campagneId);
        LocalDate today = LocalDate.now();
        exigerPeriodeOuverte(campagne, today);

        List<Long> ids = req.formationIds();
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une formation ne peut être souhaitée qu'une fois");
        }
        Map<Long, Formation> trouvees = formations.findAllById(ids).stream()
                .collect(Collectors.toMap(Formation::getId, f -> f));
        for (Long id : ids) {
            Formation f = trouvees.get(id);
            if (f == null || !f.getCampagne().getId().equals(campagneId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La formation " + id + " n'existe pas dans cette campagne");
            }
        }

        Demande d = demandes.findByUtilisateurAndCampagne(u.getId(), campagneId).orElseGet(() -> {
            Demande nouvelle = new Demande();
            nouvelle.setUtilisateur(u);
            nouvelle.setCampagne(campagne);
            return demandes.save(nouvelle);
        });
        d.setDateDerniereModif(Instant.now());
        souhaits.supprimerParDemande(d.getId());

        List<Souhait> nouveaux = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Souhait s = new Souhait();
            s.setDemande(d);
            s.setFormation(trouvees.get(ids.get(i)));
            s.setRang((short) (i + 1));
            nouveaux.add(s);
        }
        souhaits.saveAll(nouveaux);
        return DemandeResponse.from(d, nouveaux, today);
    }

    @Transactional
    public void supprimer(Jwt jwt, Long campagneId) {
        Utilisateur u = utilisateurService.courant(jwt);
        Demande d = demandes.findByUtilisateurAndCampagne(u.getId(), campagneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune demande pour cette campagne"));
        exigerPeriodeOuverte(d.getCampagne(), LocalDate.now());
        souhaits.supprimerParDemande(d.getId());
        demandes.delete(d);
    }

    private void exigerPeriodeOuverte(Campagne campagne, LocalDate today) {
        if (!CampagneService.estOuverte(campagne, today)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "La période de dépôt des demandes est du " + campagne.getDateOuverture()
                            + " au " + campagne.getDateCloture());
        }
    }
}
