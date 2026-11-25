package it.aegis.config;

import it.aegis.config.env.EnvironmentVariables;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Configurazione tipizzata. Ogni valore deriva dal file esterno e nessuno ha un
 * default silenzioso. La directory dati e i percorsi dei bundle sono inclusi
 * perché anche i testi dell'interfaccia e degli adattatori restino fuori dal codice.
 *
 * <p>Il costruttore pubblico verifica i valori obbligatori e la coerenza delle
 * grandezze numeriche. Il costruttore protetto senza argomenti consente al
 * container CDI di creare il proxy applicativo. I parametri delle integrazioni
 * possono essere vuoti quando la distribuzione non li ha ancora forniti e gli
 * adattatori li rigettano prima di qualunque effetto esterno.
 */
public class AegisConfig {

    /** Finestra di freschezza delle evidenze di accessibilità. */
    private final Duration freshnessWindow;
    /** Dimensione massima ammessa per un allegato, in byte. */
    private final long maxAttachmentBytes;
    /** Entropia richiesta per i codici di tracking, in bit. */
    private final int trackingEntropyBits;
    /** Alfabeto ordinato usato per codificare i codici di tracking. */
    private final String trackingAlphabet;
    /** Raggio terrestre impiegato nei calcoli geografici, in metri. */
    private final double earthRadiusMetres;
    /** Directory esterna dei dati applicativi. */
    private final Path dataDirectory;
    /** Percorso del bundle messaggi della vista. */
    private final String messageBundleBasePath;
    /** Impostazioni del provider di routing. */
    private final RoutingSettings routing;
    /** Impostazioni della fonte di dati di trasporto. */
    private final TransitSettings transit;
    /** Impostazioni dello storage degli allegati. */
    private final StorageSettings storage;
    /** Percorso del bundle messaggi degli adattatori. */
    private final String adapterMessageBundleBasePath;
    /** Percorso del bundle messaggi degli errori REST. */
    private final String restMessageBundleBasePath;
    /** Identificativi autorizzati a inviare segnalazioni. */
    private final Set<UUID> submitterIds;
    /** Identificativi autorizzati a moderare segnalazioni. */
    private final Set<UUID> moderatorIds;
    /** Fuso orario usato per datare eventi e segnalazioni. */
    private final ZoneId timeZone;

    /**
     * Costruttore senza argomenti richiesto dal proxy CDI.
     *
     * <p>Le istanze applicative vengono create dal caricatore di configurazione.</p>
     */
    protected AegisConfig() {
        freshnessWindow = null;
        maxAttachmentBytes = 0L;
        trackingEntropyBits = 0;
        trackingAlphabet = null;
        earthRadiusMetres = 0.0;
        dataDirectory = null;
        messageBundleBasePath = null;
        routing = null;
        transit = null;
        storage = null;
        adapterMessageBundleBasePath = null;
        restMessageBundleBasePath = null;
        submitterIds = Set.of();
        moderatorIds = Set.of();
        timeZone = null;
    }

    /**
     * Verifica che i valori obbligatori siano presenti e rispettino i vincoli attesi.
     *
     * @param freshnessWindow finestra positiva di freschezza delle evidenze
     * @param maxAttachmentBytes dimensione massima positiva degli allegati, in byte
     * @param trackingEntropyBits entropia positiva, multipla di {@link Byte#SIZE}
     * @param trackingAlphabet alfabeto non vuoto per i codici di tracking
     * @param earthRadiusMetres raggio terrestre positivo e finito, in metri
     * @param dataDirectory directory esterna dei dati applicativi
     * @param messageBundleBasePath percorso del bundle messaggi della vista
     * @param routing impostazioni del provider di routing
     * @param transit impostazioni della fonte di dati di trasporto
     * @param storage impostazioni dello storage degli allegati
     * @param adapterMessageBundleBasePath percorso del bundle messaggi degli adattatori
     * @param restMessageBundleBasePath percorso del bundle messaggi degli errori REST
     * @param submitterIds identificativi autorizzati a inviare segnalazioni
     * @param moderatorIds identificativi autorizzati a moderare segnalazioni
     * @param timeZone fuso orario per datare gli eventi
     * @throws NullPointerException se una componente obbligatoria è {@code null}
     * @throws IllegalArgumentException se la finestra, la dimensione massima, l'entropia,
     *         l'alfabeto, il percorso del bundle, il raggio terrestre o le impostazioni
     *         delle integrazioni non sono validi
     */
    public AegisConfig(
            Duration freshnessWindow, long maxAttachmentBytes, int trackingEntropyBits,
            String trackingAlphabet, double earthRadiusMetres, Path dataDirectory,
            String messageBundleBasePath, RoutingSettings routing, TransitSettings transit,
            StorageSettings storage, String adapterMessageBundleBasePath, String restMessageBundleBasePath,
            Set<UUID> submitterIds, Set<UUID> moderatorIds, ZoneId timeZone) {
        this.freshnessWindow = Objects.requireNonNull(freshnessWindow, "freshnessWindow");
        this.trackingAlphabet = Objects.requireNonNull(trackingAlphabet, "trackingAlphabet");
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
        this.messageBundleBasePath = Objects.requireNonNull(messageBundleBasePath, "messageBundleBasePath");
        this.routing = Objects.requireNonNull(routing, "routing");
        this.transit = Objects.requireNonNull(transit, "transit");
        this.storage = Objects.requireNonNull(storage, "storage");
        this.adapterMessageBundleBasePath = Objects.requireNonNull(adapterMessageBundleBasePath, "adapterMessageBundleBasePath");
        this.restMessageBundleBasePath = Objects.requireNonNull(restMessageBundleBasePath, "restMessageBundleBasePath");
        this.submitterIds = Set.copyOf(Objects.requireNonNull(submitterIds, "submitterIds"));
        this.moderatorIds = Set.copyOf(Objects.requireNonNull(moderatorIds, "moderatorIds"));
        this.timeZone = Objects.requireNonNull(timeZone, "timeZone");
        this.maxAttachmentBytes = maxAttachmentBytes;
        this.trackingEntropyBits = trackingEntropyBits;
        this.earthRadiusMetres = earthRadiusMetres;
        if (freshnessWindow.isNegative() || freshnessWindow.isZero()) {
            throw new IllegalArgumentException("freshnessWindow must be positive");
        }
        if (maxAttachmentBytes <= 0L) {
            throw new IllegalArgumentException("maxAttachmentBytes must be positive");
        }
        if (trackingEntropyBits <= 0 || trackingEntropyBits % Byte.SIZE != 0) {
            throw new IllegalArgumentException("trackingEntropyBits must be a positive multiple of Byte.SIZE");
        }
        if (trackingAlphabet.isBlank()) {
            throw new IllegalArgumentException("trackingAlphabet must not be blank");
        }
        if (messageBundleBasePath.isBlank()) {
            throw new IllegalArgumentException("messageBundleBasePath must not be blank");
        }
        if (adapterMessageBundleBasePath.isBlank()) {
            throw new IllegalArgumentException("adapterMessageBundleBasePath must not be blank");
        }
        if (restMessageBundleBasePath.isBlank()) {
            throw new IllegalArgumentException("restMessageBundleBasePath must not be blank");
        }
        if (!Double.isFinite(earthRadiusMetres) || earthRadiusMetres <= 0.0) {
            throw new IllegalArgumentException("earthRadiusMetres must be positive");
        }
    }

    /**
     * Restituisce l'intervallo massimo entro il quale un'osservazione resta utilizzabile.
     *
     * @return durata positiva applicata dai casi d'uso e dai mapper che valutano dati osservati
     */
    public Duration freshnessWindow() { return freshnessWindow; }
    /**
     * Restituisce il limite applicativo per la dimensione di un singolo allegato.
     *
     * @return limite positivo espresso in byte, già convertito dal valore esterno in mebibyte
     */
    public long maxAttachmentBytes() { return maxAttachmentBytes; }
    /**
     * Restituisce l'entropia richiesta per i codici casuali di tracking.
     *
     * @return numero positivo di bit, multiplo di {@link Byte#SIZE}, usato per derivare i byte casuali
     */
    public int trackingEntropyBits() { return trackingEntropyBits; }
    /**
     * Restituisce l'alfabeto ordinato usato per codificare i byte casuali.
     *
     * @return alfabeto non vuoto; l'ordine dei caratteri determina la rappresentazione dei codici
     */
    public String trackingAlphabet() { return trackingAlphabet; }
    /**
     * Restituisce il raggio terrestre usato nei calcoli di distanza geografica.
     *
     * @return raggio positivo e finito, espresso in metri
     */
    public double earthRadiusMetres() { return earthRadiusMetres; }
    /**
     * Restituisce la directory radice da cui sono letti i file di configurazione e i bundle.
     *
     * @return directory esterna risolta da {@link EnvironmentVariables#DATA_DIR}
     */
    public Path dataDirectory() { return dataDirectory; }
    /**
     * Restituisce il nome base del bundle usato dalla vista web.
     *
     * @return percorso relativo alla directory dati, senza lingua né suffisso del file
     */
    public String messageBundleBasePath() { return messageBundleBasePath; }
    /**
     * Restituisce i parametri esterni del servizio di routing.
     *
     * @return impostazioni non nulle; endpoint o credenziale vuoti restano non configurati e
     *         impediscono all'adattatore di inviare richieste
     */
    public RoutingSettings routing() { return routing; }
    /**
     * Restituisce la directory della fonte dati di trasporto.
     *
     * @return impostazioni non nulle; directory vuota significa che il provider non ha dati da attestare
     */
    public TransitSettings transit() { return transit; }
    /**
     * Restituisce i parametri dello storage privato degli allegati.
     *
     * @return impostazioni non nulle; valori di connessione vuoti impediscono caricamenti esterni
     */
    public StorageSettings storage() { return storage; }
    /**
     * Restituisce il nome base del bundle usato dagli adattatori esterni.
     *
     * @return percorso relativo alla directory dati, senza lingua né suffisso del file
     */
    public String adapterMessageBundleBasePath() { return adapterMessageBundleBasePath; }
    /**
     * Restituisce il nome base del bundle usato per le risposte di errore REST.
     *
     * @return percorso relativo alla directory dati, senza lingua né suffisso del file
     */
    public String restMessageBundleBasePath() { return restMessageBundleBasePath; }
    /**
     * Restituisce gli account ammessi a inviare segnalazioni.
     *
     * @return insieme immutabile; se vuoto, nessun account è autorizzato all'invio
     */
    public Set<UUID> submitterIds() { return submitterIds; }
    /**
     * Restituisce gli account ammessi a moderare segnalazioni.
     *
     * @return insieme immutabile; se vuoto, nessun account è autorizzato alla moderazione
     */
    public Set<UUID> moderatorIds() { return moderatorIds; }
    /**
     * Restituisce il fuso orario esplicito per gli istanti creati dall'applicazione.
     *
     * @return fuso validato dal caricatore; non dipende dal fuso predefinito della macchina
     */
    public ZoneId timeZone() { return timeZone; }

    /**
     * Calcola quanti byte casuali servono per l'entropia configurata.
     *
     * @return entropia in bit divisa per {@link Byte#SIZE}; la validazione garantisce divisione esatta
     */
    public int trackingTokenBytes() { return trackingEntropyBits / Byte.SIZE; }
    /**
     * Restituisce la base numerica della codifica dei codici di tracking.
     *
     * @return numero di caratteri disponibili nell'alfabeto configurato
     */
    public int trackingBase() { return trackingAlphabet.length(); }

    /**
     * Parametri di collegamento al provider di routing.
     *
     * <p>La distribuzione deve fornire entrambi i valori. Una stringa vuota
     * indica che la scelta del provider è ancora aperta e impedisce qualunque effetto
     * esterno da parte dell'adattatore.</p>
     *
     * @param baseUrl URL base del servizio di routing, anche vuoto
     * @param token credenziale inviata al servizio, anche vuota
     */
    public record RoutingSettings(String baseUrl, String token) {

        /**
         * Verifica che entrambi i parametri siano presenti come valori non nulli.
         *
         * @throws NullPointerException se {@code baseUrl} o {@code token} è {@code null}
         */
        public RoutingSettings {
            Objects.requireNonNull(baseUrl, "baseUrl");
            Objects.requireNonNull(token, "token");
        }
    }

    /**
     * Parametri della fonte di dati di trasporto.
     *
     * <p>La directory vuota indica che la fonte non è stata ancora assegnata
     * dalla distribuzione. In quel caso l'adattatore restituisce uno snapshot
     * vuoto senza alcuna affermazione di accessibilità.</p>
     *
     * @param sourceDirectory directory esterna che contiene i file della fonte, anche vuota
     */
    public record TransitSettings(String sourceDirectory) {

        /**
         * Verifica che la directory sia presente come valore non nullo.
         *
         * @throws NullPointerException se {@code sourceDirectory} è {@code null}
         */
        public TransitSettings {
            Objects.requireNonNull(sourceDirectory, "sourceDirectory");
        }
    }

    /**
     * Parametri dello storage privato degli allegati.
     *
     * <p>Endpoint, bucket e credenziale devono essere forniti dalla distribuzione.
     * L'elenco dei tipi di contenuto ammessi può essere vuoto e in quel caso
     * l'adattatore rigetta ogni caricamento.</p>
     *
     * @param endpoint endpoint del servizio compatibile con S3, anche vuoto
     * @param bucket nome del bucket privato, anche vuoto
     * @param objectPrefix prefisso logicamente separato degli oggetti, non vuoto
     * @param token credenziale inviata al servizio, anche vuota
     * @param allowedContentTypes tipi di contenuto ammessi, immutabili
     */
    public record StorageSettings(
            String endpoint,
            String bucket,
            String objectPrefix,
            String token,
            Set<String> allowedContentTypes) {

        /**
         * Verifica i valori obbligatori e rende immutabile l'elenco dei tipi ammessi.
         *
         * @throws NullPointerException se una componente è {@code null}
         */
        public StorageSettings {
            Objects.requireNonNull(endpoint, "endpoint");
            Objects.requireNonNull(bucket, "bucket");
            Objects.requireNonNull(objectPrefix, "objectPrefix");
            Objects.requireNonNull(token, "token");
            Objects.requireNonNull(allowedContentTypes, "allowedContentTypes");
            allowedContentTypes = Set.copyOf(allowedContentTypes);
        }
    }
}
