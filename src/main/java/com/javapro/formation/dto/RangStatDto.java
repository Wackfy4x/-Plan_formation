package com.javapro.formation.dto;

/** Ligne de requête du tableau de bord : souhaits exprimés et satisfaits pour un rang de préférence. */
public record RangStatDto(Short rang, Long nbSouhaits, Long nbSatisfaits) {
}
