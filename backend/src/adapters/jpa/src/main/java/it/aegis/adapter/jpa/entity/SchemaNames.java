package it.aegis.adapter.jpa.entity;

/**
 * Nomi fissi di tabelle e colonne definiti dallo schema relazionale.
 *
 * <p>Le costanti riportano i token di schema descritti nel documento di
 * progettazione. Non sono valori configurabili e non vengono costruiti a runtime.
 */
public final class SchemaNames {

    /** Tabella dei luoghi. */
    public static final String PLACE = "place";

    /** Tabella delle caratteristiche di accessibilità osservate. */
    public static final String ACCESSIBILITY_FEATURE = "accessibility_feature";

    /** Tabella delle segnalazioni. */
    public static final String REPORT = "report";

    /** Tabella degli eventi append-only delle segnalazioni. */
    public static final String REPORT_EVENT = "report_event";

    /** Tabella delle prenotazioni durevoli degli invii. */
    public static final String REPORT_SUBMISSION = "report_submission";

    /** Colonna dell'identificativo di una riga. */
    public static final String ID = "id";

    /** Colonna del nome pubblico di un luogo. */
    public static final String NAME = "name";

    /** Colonna della latitudine di un luogo. */
    public static final String LATITUDE = "latitude";

    /** Colonna della longitudine di un luogo. */
    public static final String LONGITUDE = "longitude";

    /** Colonna del riferimento al luogo di una caratteristica o di una segnalazione. */
    public static final String PLACE_ID = "place_id";

    /** Colonna del codice di una caratteristica di accessibilità. */
    public static final String CODE = "code";

    /** Colonna dello stato di una caratteristica, di un report o di una prenotazione. */
    public static final String STATUS = "status";

    /** Colonna della fonte di un'osservazione. */
    public static final String SOURCE = "source";

    /** Colonna dell'istante di osservazione di una caratteristica. */
    public static final String OBSERVED_AT = "observed_at";

    /** Colonna del codice di tracking della segnalazione o della prenotazione completata. */
    public static final String TRACKING_CODE = "tracking_code";

    /** Colonna dell'autore di una segnalazione o dello scope di una prenotazione. */
    public static final String AUTHOR_ID = "author_id";

    /** Colonna dello scope dell'account che ha creato la prenotazione. */
    public static final String SCOPE = "scope";

    /** Colonna dell'istante di creazione di una segnalazione. */
    public static final String CREATED_AT = "created_at";

    /** Colonna del riferimento a una segnalazione duplicata. */
    public static final String DUPLICATE_OF = "duplicate_of";

    /** Colonna del tipo di evento di una segnalazione. */
    public static final String EVENT_TYPE = "type";

    /** Colonna dello stato raggiunto da un evento. */
    public static final String RESULTING_STATUS = "resulting_status";

    /** Colonna della motivazione di un evento o della sua decisione. */
    public static final String REASON = "reason";

    /** Colonna dell'istante in cui si è verificato un evento. */
    public static final String OCCURRED_AT = "occurred_at";

    /** Colonna del digest della chiave di idempotenza di una prenotazione. */
    public static final String KEY_DIGEST = "key_digest";

    /** Colonna dell'impronta canonica dell'invio prenotato. */
    public static final String FINGERPRINT = "fingerprint";

    /** Colonna del riferimento al report che completa una prenotazione. */
    public static final String REPORT_ID = "report_id";

    /** Impedisce l'istanziazione di una classe che espone solo costanti di schema. */
    private SchemaNames() {
    }
}
