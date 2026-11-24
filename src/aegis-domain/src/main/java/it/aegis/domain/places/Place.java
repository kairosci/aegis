package it.aegis.domain.places;

import it.aegis.domain.accessibility.AccessibilityFeature;
import java.util.List;
import java.util.Objects;

/** Luogo consultabile con le caratteristiche di accessibilità osservate. */
public final class Place {

    private final PlaceId id;
    private final String name;
    private final GeoPoint position;
    private final List<AccessibilityFeature> features;

    public Place(PlaceId id, String name, GeoPoint position, List<AccessibilityFeature> features) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.position = Objects.requireNonNull(position, "position");
        this.features = List.copyOf(Objects.requireNonNull(features, "features"));
    }

    public PlaceId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public GeoPoint position() {
        return position;
    }

    public List<AccessibilityFeature> features() {
        return features;
    }
}
