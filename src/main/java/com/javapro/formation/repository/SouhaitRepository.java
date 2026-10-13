package com.javapro.formation.repository;

import com.javapro.formation.dto.FormationDemandeeDto;
import com.javapro.formation.dto.RangStatDto;
import com.javapro.formation.entity.Enum.StatutSouhait;
import com.javapro.formation.entity.Souhait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface SouhaitRepository extends JpaRepository<Souhait, Long> {

    @Query("select s from Souhait s join fetch s.formation where s.demande.id in :demandeIds order by s.rang")
    List<Souhait> findByDemandeIds(@Param("demandeIds") Collection<Long> demandeIds);

    @Query("""
            select s from Souhait s
              join fetch s.demande d join fetch d.utilisateur u join fetch u.service
            where s.formation.id = :formationId""")
    List<Souhait> findCandidatures(@Param("formationId") Long formationId);

    boolean existsByFormationId(Long formationId);

    long countByFormationIdAndStatut(Long formationId, StatutSouhait statut);

    /** Lignes {formationId, nombre de souhaits au statut donné}. */
    @Query("""
            select s.formation.id, count(s) from Souhait s
            where s.formation.id in :formationIds and s.statut = :statut
            group by s.formation.id""")
    List<Object[]> compterParFormation(@Param("formationIds") Collection<Long> formationIds,
                                       @Param("statut") StatutSouhait statut);

    /** Lignes {utilisateurId, nombre de souhaits au statut donné} pour une campagne. */
    @Query("""
            select s.demande.utilisateur.id, count(s) from Souhait s
            where s.demande.campagne.id = :campagneId and s.statut = :statut
            group by s.demande.utilisateur.id""")
    List<Object[]> compterParUtilisateur(@Param("campagneId") Long campagneId,
                                         @Param("statut") StatutSouhait statut);

    /** Formations auxquelles l'utilisateur est inscrit, pour les campagnes déjà publiées. */
    @Query("""
            select s from Souhait s join fetch s.formation f join fetch f.campagne c
            where s.demande.utilisateur.id = :utilisateurId and s.statut = :statut and c.datePublication <= :today
            order by f.dateFormation, f.intitule""")
    List<Souhait> findInscriptions(@Param("utilisateurId") Long utilisateurId,
                                   @Param("statut") StatutSouhait statut,
                                   @Param("today") LocalDate today);

    @Modifying(flushAutomatically = true)
    @Query("delete from Souhait s where s.demande.id = :demandeId")
    void supprimerParDemande(@Param("demandeId") Long demandeId);

    @Query("""
            select new com.javapro.formation.dto.FormationDemandeeDto(f.id, f.intitule, f.nbPlaces,
                   count(s), count(case when s.statut = :statut then 1 end))
            from Formation f left join Souhait s on s.formation = f
            where f.campagne.id = :campagneId
            group by f.id, f.intitule, f.nbPlaces
            order by count(s) desc, f.intitule""")
    List<FormationDemandeeDto> formationsLesPlusDemandees(@Param("campagneId") Long campagneId,
                                                          @Param("statut") StatutSouhait statut);

    @Query("""
            select new com.javapro.formation.dto.RangStatDto(s.rang,
                   count(s), count(case when s.statut = :statut then 1 end))
            from Souhait s
            where s.demande.campagne.id = :campagneId
            group by s.rang
            order by s.rang""")
    List<RangStatDto> statistiquesParRang(@Param("campagneId") Long campagneId,
                                          @Param("statut") StatutSouhait statut);
}
