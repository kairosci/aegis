package it.aegis.adapter.rest.dto;

import java.util.List;

/** Rappresentazione HTTP di un luogo con la lista testuale delle caratteristiche. */
public record PlaceResponse(
        String id,
        String name,
        double latitude,
        double longitude,
        List<FeatureResponse> features) {
}
