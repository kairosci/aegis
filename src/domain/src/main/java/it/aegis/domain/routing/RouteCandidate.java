package it.aegis.domain.routing;

import it.aegis.domain.places.GeoPoint;
import java.util.List;
import java.util.Objects;

/**
 * Risposta grezza del provider prima della valutazione delle evidenze.
 *
 * @param from punto di partenza del tratto
 * @param to punto di arrivo del tratto
 * @param barrierCodes codici delle barriere segnalate dal provider
 * @param providerVerified indicazione se il provider ha verificato il tratto
 */
public record RouteCandidate(
        GeoPoint from,
        GeoPoint to,
        List<String> barrierCodes,
        boolean providerVerified) {

    /**
     * Verifica che gli estremi e l'elenco delle barriere siano presenti.
     *
     * @param from punto di partenza del tratto
     * @param to punto di arrivo del tratto
     * @param barrierCodes codici delle barriere segnalate dal provider
     * @param providerVerified indicazione se il provider ha verificato il tratto
     * @throws NullPointerException se {@code from}, {@code to} o {@code barrierCodes} sono null
     */
    public RouteCandidate {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        barrierCodes = List.copyOf(Objects.requireNonNull(barrierCodes, "barrierCodes"));
    }
}
