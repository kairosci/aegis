package it.aegis.adapter.rest.dto;

/**
 * Rappresentazione HTTP di una caratteristica, distinta dal tipo di dominio.
 *
 * <p>Stato e istante di osservazione sono serializzati come testo per tenere
 * il contratto JSON indipendente dai tipi dell'aggregato.</p>
 *
 * @param code codice identificativo della caratteristica osservata
 * @param status stato della caratteristica come nome del valore di dominio
 * @param source fonte che ha fornito l'osservazione
 * @param observedAt istante dell'osservazione in formato testuale
 */
public record FeatureResponse(
        String code,
        String status,
        String source,
        String observedAt) {
}
