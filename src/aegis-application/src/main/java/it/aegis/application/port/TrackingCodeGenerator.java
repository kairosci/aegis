package it.aegis.application.port;

import it.aegis.domain.reports.TrackingCode;

/** Genera la credenziale di tracking senza esporre dati dell'autore. */
public interface TrackingCodeGenerator {

    TrackingCode generate();
}
