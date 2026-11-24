package it.aegis.adapter.jpa.mapper;

import it.aegis.adapter.jpa.entity.AccessibilityFeatureEntity;
import it.aegis.adapter.jpa.entity.PlaceEntity;
import it.aegis.domain.accessibility.AccessibilityFeature;
import it.aegis.domain.accessibility.AccessibilityStatus;
import it.aegis.domain.accessibility.FreshnessWindow;
import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversione delle righe di luogo e caratteristica nel luogo di dominio.
 *
 * <p>Il mapper riceve la finestra di validità e l'istante di riferimento dal
 * contesto applicativo. Ogni osservazione è rivalutata dal dominio, così una
 * caratteristica assente, incompleta o scaduta non diventa una prova di
 * accessibilità.
 */
public final class PlaceMapper {

    /** Impedisce l'istanziazione di una classe che espone solo metodi statici. */
    private PlaceMapper() {
    }

    /**
     * Costruisce il luogo di dominio dalla riga e dalle sue osservazioni.
     *
     * @param row riga del luogo
     * @param features osservazioni associate al luogo
     * @param window finestra di validità delle osservazioni
     * @param now istante di riferimento per la validità
     * @return luogo con caratteristiche validate
     */
    public static Place toDomain(
            PlaceEntity row,
            List<AccessibilityFeatureEntity> features,
            FreshnessWindow window,
            Instant now) {
        List<AccessibilityFeature> observed = new ArrayList<>();
        for (AccessibilityFeatureEntity feature : features) {
            AccessibilityFeature candidate = toFeature(feature, window, now);
            if (candidate != null) {
                observed.add(candidate);
            }
        }
        return new Place(
                new PlaceId(row.id()),
                row.name(),
                new GeoPoint(row.latitude(), row.longitude()),
                observed);
    }

    /**
     * Converte un'osservazione persistente solo se i dati necessari sono presenti.
     *
     * @param row riga della caratteristica osservata
     * @param window finestra di validità dell'osservazione
     * @param now istante di riferimento per la validità
     * @return caratteristica con stato effettivo, o {@code null} se la riga è incompleta
     */
    private static AccessibilityFeature toFeature(
            AccessibilityFeatureEntity row, FreshnessWindow window, Instant now) {
        if (row.id() == null
                || row.id().code() == null
                || row.id().code().isBlank()
                || row.source() == null
                || row.source().isBlank()
                || row.observedAt() == null) {
            return null;
        }
        return AccessibilityFeature.observed(
                row.id().code(),
                statusOf(row.status()),
                row.source(),
                row.observedAt(),
                window,
                now);
    }

    /**
     * Interpreta il token di stato dell'osservazione.
     *
     * @param value token letto dal database, eventualmente assente
     * @return stato corrispondente, o {@link AccessibilityStatus#UNKNOWN} se il token non è noto
     */
    private static AccessibilityStatus statusOf(String value) {
        if (value == null) {
            return AccessibilityStatus.UNKNOWN;
        }
        try {
            return AccessibilityStatus.valueOf(value);
        } catch (IllegalArgumentException cause) {
            return AccessibilityStatus.UNKNOWN;
        }
    }
}
