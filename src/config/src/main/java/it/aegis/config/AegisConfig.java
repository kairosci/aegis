package it.aegis.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;

/**
 * Configurazione tipizzata. Ogni valore deriva dal file esterno e nessuno ha un
 * default silenzioso. La directory dati e i percorsi dei bundle sono inclusi
 * perché anche i testi dell'interfaccia e degli adattatori restino fuori dal codice.
 *
 * <p>Il costruttore compatto verifica la presenza dei valori obbligatori e la
 * coerenza delle grandezze numeriche, così una configurazione errata viene
 * individuata al caricamento invece che durante l'uso. I parametri delle
 * integrazioni possono essere vuoti quando la distribuzione non li ha ancora
 * forniti e vengono rigettati dagli adattatori prima di qualunque effetto esterno.
 *
 * @param freshnessWindow finestra di freschezza delle evidenze di accessibilità, positiva
 * @param maxAttachmentBytes dimensione massima di un allegato in byte, positiva
 * @param trackingEntropyBits entropia del codice di tracking in bit, multiplo positivo di {@link Byte#SIZE}
 * @param trackingAlphabet alfabeto ordinato usato per il codice di tracking, non vuoto
 * @param earthRadiusMetres raggio terrestre usato dal calcolo geografico, positivo e finito
 * @param dataDirectory directory esterna che contiene i dati applicativi
 * @param messageBundleBasePath percorso del bundle messaggi relativo alla directory dati, non vuoto
 * @param routing parametri del provider di routing
 * @param transit parametri della fonte di dati di trasporto
 * @param storage parametri dello storage privato
 * @param adapterMessageBundleBasePath percorso del bundle dei messaggi degli adattatori, relativo alla directory dati e non vuoto
 */
public record AegisConfig(
        Duration freshnessWindow,
        long maxAttachmentBytes,
        int trackingEntropyBits,
        String trackingAlphabet,
        double earthRadiusMetres,
        Path dataDirectory,
        String messageBundleBasePath,
        RoutingSettings routing,
        TransitSettings transit,
        StorageSettings storage,
        String adapterMessageBundleBasePath) {

    /**
     * Verifica che i valori obbligatori siano presenti e rispettino i vincoli attesi.
     *
     * @throws NullPointerException se una componente obbligatoria è {@code null}
     * @throws IllegalArgumentException se la finestra, la dimensione massima, l'entropia,
     *         l'alfabeto, il percorso del bundle, il raggio terrestre o le impostazioni
     *         delle integrazioni non sono validi
     */
    public AegisConfig {
        Objects.requireNonNull(freshnessWindow, "freshnessWindow");
        Objects.requireNonNull(trackingAlphabet, "trackingAlphabet");
        Objects.requireNonNull(dataDirectory, "dataDirectory");
        Objects.requireNonNull(messageBundleBasePath, "messageBundleBasePath");
        Objects.requireNonNull(routing, "routing");
        Objects.requireNonNull(transit, "transit");
        Objects.requireNonNull(storage, "storage");
        Objects.requireNonNull(adapterMessageBundleBasePath, "adapterMessageBundleBasePath");
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
        if (!Double.isFinite(earthRadiusMetres) || earthRadiusMetres <= 0.0) {
            throw new IllegalArgumentException("earthRadiusMetres must be positive");
        }
    }

    /**
     * Restituisce il numero di byte casuali necessari per raggiungere l'entropia configurata.
     *
     * @return lunghezza in byte della sequenza casuale da generare
     */
    public int trackingTokenBytes() {
        return trackingEntropyBits / Byte.SIZE;
    }

    /**
     * Restituisce la base di codifica, pari al numero di simboli dell'alfabeto configurato.
     *
     * @return cardinalità dell'alfabeto di tracking
     */
    public int trackingBase() {
        return trackingAlphabet.length();
    }

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
