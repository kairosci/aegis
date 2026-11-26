package it.aegis.domain.places;

import it.aegis.domain.accessibility.AccessibilityFeature;
import it.aegis.domain.accessibility.FreshnessWindow;
import java.time.Instant;
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

    /**
     * Restituisce il luogo con le osservazioni classificate rispetto alla loro validità.
     *
     * @param window finestra di validità configurata
     * @param now istante comune di valutazione
     * @return copia immutabile del luogo con stati scaduti o futuri resi sconosciuti
     * @throws NullPointerException se {@code window} o {@code now} sono null
     */
    public Place withFreshness(FreshnessWindow window, Instant now) {
        Objects.requireNonNull(window, "window");
        Objects.requireNonNull(now, "now");
        List<AccessibilityFeature> currentFeatures = features.stream()
                .map(feature -> AccessibilityFeature.observed(
                        feature.code(), feature.status(), feature.source(), feature.observedAt(), window, now))
                .toList();
        return new Place(id, name, position, currentFeatures);
    }
}
