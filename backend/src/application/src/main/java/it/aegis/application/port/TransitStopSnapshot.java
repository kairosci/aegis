package it.aegis.application.port;

import it.aegis.domain.places.GeoPoint;
import java.util.List;
import java.util.Objects;

/**
 * Snapshot immutabile delle fermate restituite da una fonte di trasporto.
 *
 * <p>Il tipo trasporta esclusivamente identificazioni e coordinate. Non contiene
 * caratteristiche di accessibilità né esiti di verifica, quindi uno snapshot
 * vuoto o parziale non può essere interpretato come prova di accessibilità.</p>
 *
 * @param stops elenco immutabile delle fermate osservate
 */
public record TransitStopSnapshot(List<Stop> stops) {

    /** Snapshot vuoto, usato quando la fonte manca o non è leggibile. */
    public static final TransitStopSnapshot EMPTY = new TransitStopSnapshot(List.of());

    /**
     * Verifica che l'elenco sia presente e lo rende immutabile.
     *
     * @param stops elenco delle fermate osservate
     * @throws NullPointerException se {@code stops} è {@code null} o contiene elementi null
     */
    public TransitStopSnapshot {
        stops = List.copyOf(Objects.requireNonNull(stops, "stops"));
    }

    /**
     * Fermata osservata in una posizione geografica validata.
     *
     * @param identifier identificatore stabile nella fonte, non vuoto
     * @param name nome leggibile della fermata, non vuoto
     * @param position posizione geografica validata
     */
    public record Stop(String identifier, String name, GeoPoint position) {

        /**
         * Verifica che identificazione, nome e posizione siano presenti.
         *
         * @throws NullPointerException se una componente è {@code null}
         * @throws IllegalArgumentException se identificatore o nome sono vuoti
         */
        public Stop {
            Objects.requireNonNull(identifier, "identifier");
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(position, "position");
            if (identifier.isBlank()) {
                throw new IllegalArgumentException("identifier must not be blank");
            }
            if (name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
        }
    }
}
