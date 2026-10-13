package com.javapro.formation.service;

import com.javapro.formation.dto.CampagneRequete;
import com.javapro.formation.dto.CampagneResponse;
import com.javapro.formation.entity.Campagne;
import com.javapro.formation.repository.CampagneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampagneService {
    private final CampagneRepository campagneRepository;

    @Transactional
    public CampagneResponse create(CampagneRequete req) {
        if (campagneRepository.existsByAnnee(req.annee())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Une campagne existe déjà pour cette année");
        }
        Campagne campagne = new Campagne();
        appliquer(campagne, req);
        return CampagneResponse.from(campagneRepository.save(campagne));
    }

    @Transactional
    public CampagneResponse update(Long id, CampagneRequete req) {
        Campagne campagne = get(id);
        if (!campagne.getAnnee().equals(req.annee()) && campagneRepository.existsByAnnee(req.annee())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Une campagne existe déjà pour cette année");
        }
        appliquer(campagne, req);
        return CampagneResponse.from(campagne);
    }

    @Transactional(readOnly = true)
    public List<CampagneResponse> lister() {
        return campagneRepository.findAllByOrderByAnneeDesc().stream().map(CampagneResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CampagneResponse trouver(Long id) {
        return CampagneResponse.from(get(id));
    }

    @Transactional(readOnly = true)
    public CampagneResponse trouverCourante() {
        return CampagneResponse.from(courante());
    }

    public Campagne get(Long id) {
        return campagneRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campagne introuvable"));
    }

    /** Campagne la plus récente dont la préparation a commencé (la DRH l'établit en septembre). */
    public Campagne courante() {
        return campagneRepository.findFirstByDateEtablissementLessThanEqualOrderByAnneeDesc(LocalDate.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune campagne en cours"));
    }

    /** Période pendant laquelle les collaborateurs peuvent enregistrer et modifier leur demande. */
    public static boolean estOuverte(Campagne c, LocalDate today) {
        return !today.isBefore(c.getDateOuverture()) && !today.isAfter(c.getDateCloture());
    }

    private void appliquer(Campagne c, CampagneRequete req) {
        boolean ordreValide = !req.dateEtablissement().isAfter(req.dateOuverture())
                && req.dateOuverture().isBefore(req.dateCloture())
                && req.dateCloture().isBefore(req.datePublication());
        if (!ordreValide) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Les dates doivent respecter l'ordre : établissement <= ouverture < clôture < publication");
        }
        c.setAnnee(req.annee());
        c.setDateEtablissement(req.dateEtablissement());
        c.setDateOuverture(req.dateOuverture());
        c.setDateCloture(req.dateCloture());
        c.setDatePublication(req.datePublication());
    }
}
