package it.aegis.config.env;

/**
 * Nomi delle variabili d'ambiente riconosciute dalla configurazione.
 *
 * <p>La classe espone solo costanti di protocollo. Non memorizza valori, non
 * legge l'ambiente e non viene istanziata: i consumatori usano le costanti per
 * interrogare l'ambiente tramite le API del JDK.</p>
 */
public final class EnvironmentVariables {

    /** Nome della variabile che indica la directory esterna dei dati applicativi. */
    public static final String DATA_DIR = "AEGIS_DATA_DIR";

    /** Impedisce l'istanziazione di una classe che espone solo costanti. */
    private EnvironmentVariables() {
    }
}
