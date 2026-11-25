package it.aegis.domain.places;

import it.aegis.domain.accessibility.AccessibilityFeature;
import java.util.List;
import java.util.Objects;

/** Luogo consultabile con le caratteristiche di accessibilità osservate. */
public final class Place {

    /** Identificatore immutabile del luogo. */
    private final PlaceId id;

    /** Nome pubblico del luogo. */
    private final String name;

    /** Posizione geografica validata del luogo. */
    private final GeoPoint position;

    /** Elenco immutabile delle caratteristiche osservate per il luogo. */
    private final List<AccessibilityFeature> features;

    /**
     * Crea un luogo con elenco immutabile di caratteristiche.
     *
     * @param id identificatore del luogo
     * @param name nome pubblico del luogo
     * @param position posizione geografica del luogo
     * @param features caratteristiche di accessibilità osservate
     * @throws NullPointerException se uno degli argomenti è null
     * @throws IllegalArgumentException se il nome è vuoto o contiene solo spazi
     */
    public Place(PlaceId id, String name, GeoPoint position, List<AccessibilityFeature> features) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.position = Objects.requireNonNull(position, "position");
        this.features = List.copyOf(Objects.requireNonNull(features, "features"));
    }

    /**
     * Restituisce l'identificatore del luogo.
     *
     * @return l'identificatore immutabile del luogo
     */
    public PlaceId id() {
        return id;
    }

    /**
     * Restituisce il nome pubblico del luogo.
     *
     * @return il nome del luogo
     */
    public String name() {
        return name;
    }

    /**
     * Restituisce la posizione geografica del luogo.
     *
     * @return la posizione geografica validata
     */
    public GeoPoint position() {
        return position;
    }

    /**
     * Restituisce le caratteristiche di accessibilità osservate.
     *
     * @return elenco immutabile delle caratteristiche osservate
     */
    public List<AccessibilityFeature> features() {
        return features;
    }
}
