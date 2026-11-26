package it.aegis.application.usecase;

import it.aegis.application.port.PlaceRepository;
import it.aegis.domain.accessibility.FreshnessWindow;
import it.aegis.domain.places.Place;
import java.time.Clock;
import java.util.List;
import java.util.Objects;

/**
 * Caso d'uso di consultazione dei luoghi.
 * Classifica le osservazioni rispetto alla finestra configurata prima di restituire i risultati.
 */
public final class SearchPlaces {

    /** Porta di lettura dei luoghi. */
    private final PlaceRepository places;

    /** Finestra configurata per classificare le osservazioni di accessibilità. */
    private final FreshnessWindow freshnessWindow;

    /** Orologio usato per valutare tutte le osservazioni della ricerca allo stesso istante. */
    private final Clock clock;

    /**
     * Crea il caso d'uso con la porta di lettura da utilizzare.
     *
     * @param places porta di lettura dei luoghi
     * @param freshnessWindow finestra configurata per la validità delle osservazioni
     * @param clock orologio applicativo
     * @throws NullPointerException se una dipendenza è {@code null}
     */
    public SearchPlaces(PlaceRepository places, FreshnessWindow freshnessWindow, Clock clock) {
        this.places = Objects.requireNonNull(places, "places");
        this.freshnessWindow = Objects.requireNonNull(freshnessWindow, "freshnessWindow");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Cerca i luoghi entro il raggio richiesto.
     *
     * @param query centro e raggio della ricerca geospaziale
     * @return elenco dei luoghi trovati, eventualmente vuoto
     * @throws NullPointerException se {@code query} è {@code null}
     */
    public List<Place> search(SearchPlacesQuery query) {
        Objects.requireNonNull(query, "query");
        var evaluatedAt = clock.instant();
        return places.search(query.centre(), query.radiusMetres()).stream()
                .map(place -> place.withFreshness(freshnessWindow, evaluatedAt))
                .toList();
    }
}
