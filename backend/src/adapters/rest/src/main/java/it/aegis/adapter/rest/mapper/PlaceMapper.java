package it.aegis.adapter.rest.mapper;

import it.aegis.adapter.rest.dto.FeatureResponse;
import it.aegis.adapter.rest.dto.PlaceResponse;
import it.aegis.domain.places.Place;
import java.util.List;

/** Conversione esplicita dal dominio ai DTO HTTP. */
public final class PlaceMapper {

    /** Impedisce la creazione di istanze di una classe di sola utilità. */
    private PlaceMapper() {
    }

    /**
     * Converte un luogo e le sue caratteristiche nella risposta HTTP.
     *
     * <p>Ogni caratteristica conserva codice, fonte e istante di osservazione;
     * lo stato viene espresso con il nome del valore dell'enumerazione.</p>
     *
     * @param place luogo del dominio da convertire
     * @return rappresentazione HTTP del luogo con le caratteristiche osservate
     */
    public static PlaceResponse toResponse(Place place) {
        List<FeatureResponse> features = place.features().stream()
                .map(feature -> new FeatureResponse(
                        feature.code(),
                        feature.status().name(),
                        feature.source(),
                        feature.observedAt().toString()))
                .toList();
        return new PlaceResponse(
                place.id().toString(),
                place.name(),
                place.position().latitude(),
                place.position().longitude(),
                features);
    }
}
