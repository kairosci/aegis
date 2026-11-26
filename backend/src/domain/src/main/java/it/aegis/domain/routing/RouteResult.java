package it.aegis.domain.routing;

import java.util.List;
import java.util.Objects;

/** Percorso con stati derivati dai tratti, senza campi di stato duplicati. */
public final class RouteResult {

    /** Elenco immutabile dei tratti che compongono il percorso. */
    private final List<RouteSegment> segments;

    /**
     * Crea un risultato con elenco immutabile di tratti.
     *
     * @param segments tratti valutati che compongono il percorso
     * @throws NullPointerException se {@code segments} è null
     */
    public RouteResult(List<RouteSegment> segments) {
        this.segments = List.copyOf(Objects.requireNonNull(segments, "segments"));
    }

    /**
     * Restituisce i tratti valutati che compongono il percorso.
     *
     * @return elenco immutabile dei tratti
     */
    public List<RouteSegment> segments() {
        return segments;
    }

    /**
     * Indica se tutti i tratti sono stati verificati dal provider.
     *
     * @return {@code true} se nessun tratto ha compatibilità non verificata
     */
    public boolean isVerified() {
        return segments.stream().allMatch(segment ->
                segment.compatibility() != RouteCompatibility.UNVERIFIED);
    }

    /**
     * Indica se il percorso contiene almeno un tratto compatibile.
     *
     * @return {@code true} se almeno un tratto ha compatibilità compatibile
     */
    public boolean hasCompatibleOption() {
        return segments.stream().anyMatch(segment ->
                segment.compatibility() == RouteCompatibility.COMPATIBLE);
    }
}
