package it.aegis.application.usecase;

import it.aegis.application.port.PlaceRepository;
import it.aegis.domain.accessibility.FreshnessWindow;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import java.time.Clock;
import java.util.Objects;

/**
 * Caso d'uso di consultazione del dettaglio di un luogo.
 * Recupera il luogo tramite la porta di lettura, classifica le osservazioni
 * rispetto alla finestra configurata e segnala un identificativo sconosciuto.
 */
public final class FindPlace {

    /** Porta di lettura dei luoghi. */
    private final PlaceRepository places;

    /** Finestra configurata per classificare le osservazioni di accessibilità. */
    private final FreshnessWindow freshnessWindow;

    /** Orologio usato per valutare le osservazioni. */
    private final Clock clock;

    /**
     * Crea il caso d'uso con la porta di lettura da utilizzare.
     *
     * @param places porta di lettura dei luoghi
     * @param freshnessWindow finestra configurata per la validità delle osservazioni
     * @param clock orologio applicativo
     * @throws NullPointerException se una dipendenza è {@code null}
     */
    public FindPlace(PlaceRepository places, FreshnessWindow freshnessWindow, Clock clock) {
        this.places = Objects.requireNonNull(places, "places");
        this.freshnessWindow = Objects.requireNonNull(freshnessWindow, "freshnessWindow");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Restituisce il dettaglio del luogo richiesto.
     *
     * @param id identificativo del luogo
     * @return luogo corrispondente all'identificativo
     * @throws NullPointerException se {@code id} è {@code null}
     * @throws IllegalArgumentException se non esiste alcun luogo con quell'identificativo
     */
    public Place find(PlaceId id) {
        Objects.requireNonNull(id, "id");
        Place place = places.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("place not found"));
        return place.withFreshness(freshnessWindow, clock.instant());
    }
}
