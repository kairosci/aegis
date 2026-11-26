package it.aegis.adapter.rest;

/**
 * Costanti di protocollo condivise dalle risorse HTTP.
 *
 * <p>La classe raccoglie i segmenti di percorso e i nomi dei parametri usati
 * dal contratto REST. Sono token fissi del protocollo, non valori configurabili
 * né testi destinati agli utenti.</p>
 */
public final class RestContract {

    /** Segmento di percorso delle risorse dedicate ai luoghi. */
    public static final String PLACES_PATH = "/places";

    /** Segmento di percorso che identifica una singola risorsa. */
    public static final String ID_PATH = "{id}";

    /** Segmento di percorso che identifica una segnalazione tramite codice di tracking. */
    public static final String CODE_PATH = "{code}";

    /** Segmento di percorso delle risorse dedicate alle segnalazioni. */
    public static final String REPORTS_PATH = "/reports";

    /** Segmento di percorso della decisione di moderazione di una segnalazione. */
    public static final String DECISION_PATH = "{id}/decision";

    /** Segmento di percorso delle risorse dedicate alla pianificazione dei percorsi. */
    public static final String ROUTES_PATH = "/routes";

    /** Nome del parametro di percorso che contiene l'identificativo della risorsa. */
    public static final String ID_PARAM = "id";

    /** Nome del parametro di percorso che contiene il codice di tracking. */
    public static final String CODE_PARAM = "code";

    /** Nome del parametro di query che contiene il luogo di partenza. */
    public static final String ORIGIN_PARAM = "origin";

    /** Nome del parametro di query che contiene il luogo di arrivo. */
    public static final String DESTINATION_PARAM = "destination";

    /** Impedisce la creazione di istanze di una classe che espone solo costanti. */
    private RestContract() {
    }
}
