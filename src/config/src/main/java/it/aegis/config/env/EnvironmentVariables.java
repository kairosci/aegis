package it.aegis.config.env;

/**
 * Nomi delle variabili d'ambiente usate dall'applicazione.
 *
 * <p>Ogni nome è una costante di protocollo condivisa dai componenti che leggono
 * l'ambiente. Nessuna classe all'esterno di questo package ripete il valore testuale.
 */
public final class EnvironmentVariables {

    /** Nome della variabile che indica la directory esterna dei dati applicativi. */
    public static final String DATA_DIR = "AEGIS_DATA_DIR";

    /** Impedisce l'istanziazione di una classe che espone solo costanti. */
    private EnvironmentVariables() {
    }
}
