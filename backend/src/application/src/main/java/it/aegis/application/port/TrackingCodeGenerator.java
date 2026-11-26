package it.aegis.application.port;

import it.aegis.domain.reports.TrackingCode;

/**
 * Genera la credenziale di tracking senza esporre dati dell'autore.
 * Il codice prodotto è l'unico riferimento pubblico alla segnalazione.
 */
public interface TrackingCodeGenerator {

    /**
     * Genera un nuovo codice di tracking per la segnalazione da creare.
     *
     * @return codice di tracking appena generato
     */
    TrackingCode generate();
}
