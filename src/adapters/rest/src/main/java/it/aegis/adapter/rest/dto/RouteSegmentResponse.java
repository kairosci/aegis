package it.aegis.adapter.rest.dto;

import java.util.List;
import java.util.Objects;

/**
 * Rappresentazione HTTP di un tratto di percorso valutato.
 *
 * <p>Gli estremi sono espressi con coordinate primitive e la compatibilità come
 * nome del valore di dominio, così il contratto JSON non dipende dai tipi
 * interni. I codici barriera restano identificatori testuali forniti dal
 * provider.</p>
 *
 * @param fromLatitude latitudine del punto di partenza in gradi
 * @param fromLongitude longitudine del punto di partenza in gradi
 * @param toLatitude latitudine del punto di arrivo in gradi
 * @param toLongitude longitudine del punto di arrivo in gradi
 * @param compatibility compatibilità derivata dalle evidenze come nome del valore di dominio
 * @param barrierCodes codici delle barriere osservate sul tratto
 */
public record RouteSegmentResponse(
        double fromLatitude,
        double fromLongitude,
        double toLatitude,
        double toLongitude,
        String compatibility,
        List<String> barrierCodes) {

    /**
     * Crea una risposta con elenco immutabile di codici barriera.
     *
     * @param fromLatitude latitudine del punto di partenza in gradi
     * @param fromLongitude longitudine del punto di partenza in gradi
     * @param toLatitude latitudine del punto di arrivo in gradi
     * @param toLongitude longitudine del punto di arrivo in gradi
     * @param compatibility compatibilità derivata dalle evidenze come nome del valore di dominio
     * @param barrierCodes codici delle barriere osservate sul tratto
     * @throws NullPointerException se {@code compatibility} o {@code barrierCodes} è {@code null}
     */
    public RouteSegmentResponse {
        Objects.requireNonNull(compatibility);
        barrierCodes = List.copyOf(barrierCodes);
    }
}
