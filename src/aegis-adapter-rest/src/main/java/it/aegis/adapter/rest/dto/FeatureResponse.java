package it.aegis.adapter.rest.dto;

/** Rappresentazione HTTP di una caratteristica, distinta dal tipo di dominio. */
public record FeatureResponse(
        String code,
        String status,
        String source,
        String observedAt) {
}
