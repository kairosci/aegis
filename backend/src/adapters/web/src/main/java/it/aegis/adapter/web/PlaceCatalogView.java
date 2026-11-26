package it.aegis.adapter.web;

import it.aegis.application.usecase.SearchPlaces;
import it.aegis.application.usecase.SearchPlacesQuery;
import it.aegis.config.AegisConfig;
import it.aegis.config.MessageSource;
import it.aegis.domain.accessibility.AccessibilityFeature;
import it.aegis.domain.accessibility.AccessibilityStatus;
import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

/** Modello della vista di consultazione dei luoghi. */
@Named
@RequestScoped
public class PlaceCatalogView {

    /** Caso d'uso che cerca luoghi nell'area richiesta. */
    private final SearchPlaces searchPlaces;

    /** Sorgente dei messaggi localizzati. */
    private final MessageSource messages;

    /** Configurazione della validità delle osservazioni e del fuso orario. */
    private final AegisConfig config;

    /** Latitudine del centro dell'area di ricerca. */
    private Double latitude;

    /** Longitudine del centro dell'area di ricerca. */
    private Double longitude;

    /** Raggio dell'area di ricerca in metri. */
    private Integer radiusMetres;

    /** Luoghi trovati dall'ultima ricerca valida. */
    private List<Place> results = List.of();

    /** Indica se l'utente ha già richiesto una ricerca. */
    private boolean searched;

    /** Errore di input restituito dal caso d'uso, se presente. */
    private String errorKey;

    /**
     * Crea il modello della vista con dipendenze applicative esplicite.
     *
     * @param searchPlaces caso d'uso di ricerca geospaziale
     * @param messages sorgente dei testi della vista
     * @param config configurazione applicativa
     */
    @Inject
    public PlaceCatalogView(
            SearchPlaces searchPlaces,
            MessageSource messages,
            AegisConfig config) {
        this.searchPlaces = searchPlaces;
        this.messages = messages;
        this.config = config;
    }

    /** Esegue la ricerca nell'area indicata dall'utente. */
    public void search() {
        searched = true;
        errorKey = null;
        if (latitude == null || longitude == null || radiusMetres == null) {
            results = List.of();
            errorKey = "places.invalid-area";
            return;
        }
        try {
            GeoPoint centre = new GeoPoint(latitude, longitude);
            results = searchPlaces.search(new SearchPlacesQuery(centre, radiusMetres));
        } catch (IllegalArgumentException cause) {
            results = List.of();
            errorKey = "places.invalid-area";
        }
    }

    /**
     * Restituisce un messaggio della vista in italiano.
     *
     * @param key chiave logica del messaggio
     * @return testo associato alla chiave
     */
    public String text(String key) {
        return messages.get(key, Locale.ITALIAN);
    }

    /** Restituisce l'etichetta italiana dello stato canonico dell'osservazione. */
    public String featureStatus(AccessibilityFeature feature) {
        String key = switch (feature.status()) {
            case AVAILABLE -> "places.status.available";
            case UNAVAILABLE -> "places.status.unavailable";
            case UNKNOWN -> "places.status.unknown";
        };
        return text(key);
    }

    /**
     * Formatta la data dell'osservazione nella lingua della vista.
     *
     * @param instant istante da mostrare
     * @return data e ora nel fuso orario configurato
     */
    public String formatDate(Instant instant) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.ITALIAN)
                .withZone(config.timeZone())
                .format(instant);
    }

    /**
     * Calcola la scadenza dell'osservazione usando la finestra configurata.
     *
     * @param feature caratteristica osservata
     * @return data e ora di scadenza nel fuso orario configurato
     */
    public String featureValidUntil(AccessibilityFeature feature) {
        return formatDate(feature.observedAt().plus(config.freshnessWindow()));
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadiusMetres() {
        return radiusMetres;
    }

    public void setRadiusMetres(Integer radiusMetres) {
        this.radiusMetres = radiusMetres;
    }

    public List<Place> getResults() {
        return results;
    }

    public boolean isSearched() {
        return searched;
    }

    public String getErrorMessage() {
        return errorKey == null ? null : text(errorKey);
    }
}
