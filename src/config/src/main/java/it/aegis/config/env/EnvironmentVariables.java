package it.aegis.config.env;

/**
 * Nomi delle variabili d'ambiente riconosciute dalla configurazione.
 *
 * <p>La classe espone solo costanti di protocollo: non legge valori, non
 * applica default e non contiene percorsi o dati configurabili. I nomi sono
 * mantenuti qui perché ogni lettura di una variabile d'ambiente nei moduli
 * usi la stessa costante condivisa.</p>
 */
public final class EnvironmentVariables {

    /** Nome della variabile che indica la directory dati esterna al codice. */
    public static final String DATA_DIR = "AEGIS_DATA_DIR";

    /** Impedisce la creazione di istanze di una classe che espone solo costanti. */
    private EnvironmentVariables() {
    }
}
