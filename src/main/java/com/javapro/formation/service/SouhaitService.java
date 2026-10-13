package com.javapro.formation.service;

import com.javapro.formation.dto.CandidatureResponse;
import com.javapro.formation.dto.CandidaturesResponse;
import com.javapro.formation.dto.InscriptionResponse;
import com.javapro.formation.dto.StatutRequete;
import com.javapro.formation.dto.ValidationRequete;
import com.javapro.formation.entity.Enum.StatutSouhait;
import com.javapro.formation.entity.Formation;
import com.javapro.formation.entity.Souhait;
import com.javapro.formation.entity.Utilisateur;
import com.javapro.formation.repository.FormationRepository;
import com.javapro.formation.repository.FormationServicePrioritaireRepository;
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

/** Traitement des souhaits par la DRH (validation, inscriptions) et consultation des inscriptions par le collaborateur. */
@Service
@RequiredArgsConstructor
public class SouhaitService {
    /** En principe, pas plus de 2 formations par an et par collaborateur ; la DRH peut faire des exceptions. */
    private static final int FORMATIONS_PAR_AN = 2;

    private final SouhaitRepository souhaits;
    private final FormationRepository formations;
    private final FormationServicePrioritaireRepository prioritaires;
    private final UtilisateurService utilisateurService;

    /** Calendrier du collaborateur : ses formations accordées, une fois la campagne publiée. */
    @Transactional(readOnly = true)
    public List<InscriptionResponse> mesInscriptions(Jwt jwt, boolean aVenir) {
        Utilisateur u = utilisateurService.courant(jwt);
        LocalDate today = LocalDate.now();
        return souhaits.findInscriptions(u.getId(), StatutSouhait.INSCRIT, today).stream()
                .map(Souhait::getFormation)
                .filter(f -> !aVenir || f.getDateFormation() == null || !f.getDateFormation().isBefore(today))
                .map(InscriptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CandidaturesResponse candidatures(Long formationId) {
        Formation f = formations.findById(formationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation introuvable"));
        return construire(f);
    }

    /**
     * Valide globalement une formation : les souhaits retenus passent « inscrit », les autres candidatures
     * « mis en attente ». Les souhaits déjà « désinscrit » ne sont pas touchés.
     */
    @Transactional
    public CandidaturesResponse valider(Jwt jwt, Long formationId, ValidationRequete req) {
        Utilisateur drh = utilisateurService.courant(jwt);
        Formation f = verrouiller(formationId);
        exigerCampagneCloturee(f);

        Map<Long, Souhait> candidatures = souhaits.findCandidatures(formationId).stream()
                .collect(Collectors.toMap(Souhait::getId, s -> s));
        Set<Long> retenus = new HashSet<>(req.souhaitIdsRetenus());
        for (Long id : retenus) {
            Souhait s = candidatures.get(id);
            if (s == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le souhait " + id + " ne concerne pas cette formation");
            }
            if (s.getStatut() == StatutSouhait.DESINSCRIT) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le souhait " + id + " est désinscrit");
            }
        }
        if (retenus.size() > f.getNbPlaces()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Le nombre de candidats retenus dépasse la capacité (" + f.getNbPlaces() + ")");
        }

        Instant now = Instant.now();
        for (Souhait s : candidatures.values()) {
            if (s.getStatut() != StatutSouhait.DESINSCRIT) {
                decider(s, retenus.contains(s.getId()) ? StatutSouhait.INSCRIT : StatutSouhait.MIS_EN_ATTENTE, drh, now);
            }
        }
        f.setEstValidee(true);
        f.setDateValidation(now);
        f.setValidePar(drh);
        return construire(f);
    }

    /**
     * Modification individuelle après la validation : désinscrire un collaborateur (la place se libère),
     * inscrire un collaborateur mis en attente, ou remettre un souhait en attente.
     */
    @Transactional
    public CandidaturesResponse changerStatut(Jwt jwt, Long souhaitId, StatutRequete req) {
        Utilisateur drh = utilisateurService.courant(jwt);
        Souhait s = souhaits.findById(souhaitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Souhait introuvable"));
        Formation f = verrouiller(s.getFormation().getId());
        exigerCampagneCloturee(f);

        StatutSouhait statut = req.statut();
        if (statut == StatutSouhait.EN_ATTENTE_VALIDATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Statuts possibles : INSCRIT, MIS_EN_ATTENTE ou DESINSCRIT");
        }
        if (statut == StatutSouhait.INSCRIT && s.getStatut() != StatutSouhait.INSCRIT
                && souhaits.countByFormationIdAndStatut(f.getId(), StatutSouhait.INSCRIT) >= f.getNbPlaces()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La formation est complète");
        }
        decider(s, statut, drh, Instant.now());
        return construire(f);
    }

    private Formation verrouiller(Long formationId) {
        return formations.findByIdForUpdate(formationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation introuvable"));
    }

    private void exigerCampagneCloturee(Formation f) {
        if (!LocalDate.now().isAfter(f.getCampagne().getDateCloture())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Les demandes ne peuvent être traitées qu'après la clôture (" + f.getCampagne().getDateCloture() + ")");
        }
    }

    private void decider(Souhait s, StatutSouhait statut, Utilisateur drh, Instant date) {
        s.setStatut(statut);
        s.setDateDecision(date);
        s.setDecidePar(drh);
    }

    /** Vue DRH d'une formation : candidats triés (service prioritaire d'abord, puis préférence), avec service et quota. */
    private CandidaturesResponse construire(Formation f) {
        Set<Long> servicesPrioritaires = prioritaires.findByFormationIds(List.of(f.getId())).stream()
                .map(p -> p.getService().getId())
                .collect(Collectors.toSet());
        Map<Long, Long> accordees = souhaits.compterParUtilisateur(f.getCampagne().getId(), StatutSouhait.INSCRIT)
                .stream()
                .collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue()));

        List<Souhait> liste = souhaits.findCandidatures(f.getId());
        List<CandidatureResponse> candidatures = liste.stream()
                .map(s -> {
                    Utilisateur u = s.getDemande().getUtilisateur();
                    return new CandidatureResponse(s.getId(), u.getId(), u.getNom(), u.getPrenom(),
                            u.getService().getNom(), servicesPrioritaires.contains(u.getService().getId()),
                            s.getRang(), s.getStatut(), accordees.getOrDefault(u.getId(), 0L));
                })
                .sorted(Comparator.comparing(CandidatureResponse::serviceEstPrioritaire, Comparator.reverseOrder())
                        .thenComparing(CandidatureResponse::rang)
                        .thenComparing(CandidatureResponse::souhaitId))
                .toList();

        List<String> avertissements = candidatures.stream()
                .filter(c -> c.statut() == StatutSouhait.INSCRIT && c.nbFormationsAccordees() > FORMATIONS_PAR_AN)
                .map(c -> c.prenom() + " " + c.nom() + " a " + c.nbFormationsAccordees()
                        + " formations accordées (" + FORMATIONS_PAR_AN + " en principe)")
                .toList();
        long nbInscrits = candidatures.stream().filter(c -> c.statut() == StatutSouhait.INSCRIT).count();
        return new CandidaturesResponse(f.getId(), f.getIntitule(), f.getNbPlaces(), nbInscrits,
                f.isEstValidee(), candidatures, avertissements);
    }
}
