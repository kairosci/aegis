package it.aegis.domain.routing;

import it.aegis.domain.places.GeoPoint;
import java.util.List;
import java.util.Objects;

/**
 * Tratto di percorso con esito di compatibilità derivato dalle evidenze.
 *
 * @param from punto di partenza del tratto
 * @param to punto di arrivo del tratto
 * @param compatibility compatibilità derivata dalle evidenze
 * @param barrierCodes codici delle barriere osservate sul tratto
 */
public record RouteSegment(
        GeoPoint from,
        GeoPoint to,
        RouteCompatibility compatibility,
        List<String> barrierCodes) {

    /**
     * Verifica che estremi, compatibilità ed elenco delle barriere siano presenti.
     *
     * @param from punto di partenza del tratto
     * @param to punto di arrivo del tratto
     * @param compatibility compatibilità derivata dalle evidenze
     * @param barrierCodes codici delle barriere osservate sul tratto
     * @throws NullPointerException se uno degli argomenti obbligatori è null
     */
    public RouteSegment {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(compatibility, "compatibility");
        barrierCodes = List.copyOf(Objects.requireNonNull(barrierCodes, "barrierCodes"));
    }
}
