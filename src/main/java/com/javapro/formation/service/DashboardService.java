package com.javapro.formation.service;

import com.javapro.formation.dto.RangStatDto;
import com.javapro.formation.dto.TableauDeBordResponse;
import com.javapro.formation.dto.TableauDeBordResponse.TauxRang;
import com.javapro.formation.entity.Campagne;
import com.javapro.formation.entity.Enum.StatutSouhait;
import com.javapro.formation.repository.SouhaitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final SouhaitRepository souhaits;
    private final CampagneService campagneService;

    @Transactional(readOnly = true)
    public TableauDeBordResponse tableauDeBord(Long campagneId) {
        Campagne campagne = campagneId != null ? campagneService.get(campagneId) : campagneService.courante();
        List<RangStatDto> stats = souhaits.statistiquesParRang(campagne.getId(), StatutSouhait.INSCRIT);

        List<TauxRang> parRang = stats.stream()
                .map(s -> new TauxRang(s.rang(), s.nbSouhaits(), s.nbSatisfaits(), pourcentage(s.nbSatisfaits(), s.nbSouhaits())))
                .toList();
        long total = stats.stream().mapToLong(RangStatDto::nbSouhaits).sum();
        long satisfaits = stats.stream().mapToLong(RangStatDto::nbSatisfaits).sum();

        return new TableauDeBordResponse(campagne.getId(), campagne.getAnnee(),
                souhaits.formationsLesPlusDemandees(campagne.getId(), StatutSouhait.INSCRIT),
                parRang, pourcentage(satisfaits, total));
    }

    private double pourcentage(long partie, long total) {
        return total == 0 ? 0 : Math.round(partie * 1000.0 / total) / 10.0;
    }
}
