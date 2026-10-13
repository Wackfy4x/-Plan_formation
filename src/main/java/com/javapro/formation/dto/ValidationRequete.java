package com.javapro.formation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Souhaits retenus pour la formation : ils passent « inscrit », les autres candidats « mis en attente ». */
public record ValidationRequete(@NotNull List<Long> souhaitIdsRetenus) {
}
