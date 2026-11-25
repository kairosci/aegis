package it.aegis.adapter.rest.dto;

import java.util.List;

/**
 * Rappresentazione HTTP di un luogo con la lista testuale delle caratteristiche.
 *
 * <p>La posizione è espressa con coordinate primitive e l'identificatore come
 * testo, così la risposta non espone direttamente i tipi del dominio.</p>
 *
 * @param id identificatore del luogo in formato testuale
 * @param name nome pubblico del luogo
 * @param latitude latitudine del luogo in gradi
 * @param longitude longitudine del luogo in gradi
 * @param features caratteristiche di accessibilità nella rappresentazione HTTP
 */
public record PlaceResponse(
        String id,
        String name,
        double latitude,
        double longitude,
        List<FeatureResponse> features) {
}
