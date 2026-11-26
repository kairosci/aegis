package it.aegis.config.env;

/**
 * Nomi delle variabili d'ambiente riconosciute dalla configurazione applicativa.
 *
 * <p>Le costanti sono dichiarate solo in questo package. I chiamanti devono
 * usarle come riferimenti, così un nome non viene ripetuto in più classi e
 * una modifica resta limitata a questo punto.
 */
public final class EnvironmentVariables {

    /** Nome della variabile che indica la directory esterna dei dati applicativi. */
    public static final String DATA_DIR = "AEGIS_DATA_DIR";

    /** Impedisce l'istanziazione di una classe che espone solo costanti. */
    private EnvironmentVariables() {
    }
}
