package it.aegis.adapter.rest.dto;

/**
 * Rappresentazione uniforme degli errori HTTP.
 *
 * <p>Il codice identifica la classe dell'errore indipendentemente dalla lingua,
 * mentre il messaggio è risolto dalla sorgente messaggi configurata. La
 * risposta non contiene dettagli interni né dati personali.</p>
 *
 * @param code codice stabile della classe di errore
 * @param message messaggio localizzato destinato al client
 */
public record ErrorResponse(String code, String message) {
}
