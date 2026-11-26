package it.aegis.config;

import it.aegis.config.env.EnvironmentVariables;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 * Carica la configurazione dalla directory dati esterna al codice, senza valori predefiniti.
 *
 * <p>La directory è indicata da una variabile d'ambiente e il file è letto in UTF-8.
 * Ogni chiave attesa deve essere presente e interpretabile: un valore mancante o
 * non valido interrompe il caricamento invece di essere sostituito da un default.
 * I parametri delle integrazioni sono presenti ma possono essere vuoti quando la
 * distribuzione non li ha ancora assegnati.</p>
 */
public final class ConfigLoader {

    /** Nome del file di configurazione letto nella directory dati. */
    private static final String CONFIG_FILE = "application.properties";

    /** Chiave della finestra di freschezza delle evidenze, espressa in giorni. */
    private static final String FRESHNESS_DAYS = "aegis.accessibility.freshness-days";

    /** Chiave della dimensione massima di un allegato, espressa in mebibyte. */
    private static final String ATTACHMENT_MEGABYTES = "aegis.reports.max-attachment-megabytes";

    /** Chiave dell'entropia del codice di tracking, espressa in bit. */
    private static final String TRACKING_ENTROPY_BITS = "aegis.tracking.entropy-bits";

    /** Chiave dell'alfabeto ordinato usato per codificare il codice di tracking. */
    private static final String TRACKING_ALPHABET = "aegis.tracking.alphabet";

    /** Chiave del raggio terrestre usato dal calcolo geografico, in metri. */
    private static final String EARTH_RADIUS_METRES = "aegis.geo.earth-radius-metres";

    /** Chiave del percorso del bundle dei messaggi, relativo alla directory dati. */
    private static final String MESSAGE_BUNDLE_BASE_PATH = "aegis.messages.base-path";

    /** Chiave del percorso del bundle dei messaggi degli adattatori. */
    private static final String ADAPTER_MESSAGE_BUNDLE_BASE_PATH = "aegis.adapter.messages.base-path";

    /** Chiave del percorso del bundle degli errori REST. */
    private static final String REST_MESSAGE_BUNDLE_BASE_PATH = "aegis.rest.messages.base-path";

    /** Chiave dell'URL base del servizio di routing. */
    private static final String ROUTING_BASE_URL = "aegis.routing.base-url";

    /** Chiave della credenziale del servizio di routing. */
    private static final String ROUTING_TOKEN = "aegis.routing.token";

    /** Chiave della directory esterna della fonte di dati di trasporto. */
    private static final String TRANSIT_SOURCE_DIRECTORY = "aegis.transit.source-directory";

    /** Chiave dell'endpoint del servizio di storage compatibile con S3. */
    private static final String STORAGE_ENDPOINT = "aegis.storage.endpoint";

    /** Chiave del bucket privato usato dallo storage. */
    private static final String STORAGE_BUCKET = "aegis.storage.bucket";

    /** Chiave del prefisso degli oggetti scritti dallo storage. */
    private static final String STORAGE_OBJECT_PREFIX = "aegis.storage.object-prefix";

    /** Chiave della credenziale del servizio di storage. */
    private static final String STORAGE_TOKEN = "aegis.storage.token";

    /** Chiave dell'elenco dei tipi di contenuto ammessi dallo storage. */
    private static final String STORAGE_ALLOWED_CONTENT_TYPES = "aegis.storage.allowed-content-types";

    /** Chiave degli identificativi autorizzati a inviare segnalazioni. */
    private static final String SUBMITTER_IDS = "aegis.authorization.submitters";
    /** Chiave degli identificativi autorizzati a moderare segnalazioni. */
    private static final String MODERATOR_IDS = "aegis.authorization.moderators";
    /** Chiave del fuso orario usato per datare gli eventi. */
    private static final String TIME_ZONE = "aegis.time.zone-id";

    /** Fattore binario elevato al quadrato per convertire i mebibyte in byte. */
    private static final long BINARY_PREFIX_BASE = 1024L;

    /** Separatore dei valori nell'elenco configurato dei tipi di contenuto. */
    private static final String VALUE_SEPARATOR = ",";

    /** Impedisce l'istanziazione di una classe che espone solo metodi statici. */
    private ConfigLoader() {
    }

    /**
     * Carica la configurazione dal file {@value #CONFIG_FILE} nella directory indicata
     * da {@link EnvironmentVariables#DATA_DIR}. Tutte le chiavi attese devono essere
     * presenti e interpretabili; non vengono applicati valori predefiniti.
     *
     * @return configurazione tipizzata derivata dal file esterno
     * @throws IllegalStateException se la variabile d'ambiente non è impostata, il file non
     *         è leggibile oppure una chiave è assente o non valida
     * @throws ArithmeticException se la conversione della dimensione massima degli allegati
     *         supera l'intervallo di {@code long}
     */
    public static AegisConfig load() {
        String directory = System.getenv(EnvironmentVariables.DATA_DIR);
        if (directory == null || directory.isBlank()) {
            throw new IllegalStateException(EnvironmentVariables.DATA_DIR + " must be set");
        }
        Path dataDirectory = Path.of(directory);
        Path file = dataDirectory.resolve(CONFIG_FILE);
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException cause) {
            throw new IllegalStateException("cannot read " + file, cause);
        }

        long megabytes = requiredLong(properties, ATTACHMENT_MEGABYTES);
        long bytesPerMebibyte = BINARY_PREFIX_BASE * BINARY_PREFIX_BASE;
        return new AegisConfig(
                Duration.ofDays(requiredLong(properties, FRESHNESS_DAYS)),
                Math.multiplyExact(megabytes, bytesPerMebibyte),
                requiredInt(properties, TRACKING_ENTROPY_BITS),
                required(properties, TRACKING_ALPHABET),
                requiredDouble(properties, EARTH_RADIUS_METRES),
                dataDirectory,
                required(properties, MESSAGE_BUNDLE_BASE_PATH),
                new AegisConfig.RoutingSettings(
                        configured(properties, ROUTING_BASE_URL),
                        configured(properties, ROUTING_TOKEN)),
                new AegisConfig.TransitSettings(configured(properties, TRANSIT_SOURCE_DIRECTORY)),
                new AegisConfig.StorageSettings(
                        configured(properties, STORAGE_ENDPOINT),
                        configured(properties, STORAGE_BUCKET),
                        configured(properties, STORAGE_OBJECT_PREFIX),
                        configured(properties, STORAGE_TOKEN),
                        configuredSet(properties, STORAGE_ALLOWED_CONTENT_TYPES)),
                required(properties, ADAPTER_MESSAGE_BUNDLE_BASE_PATH),
                required(properties, REST_MESSAGE_BUNDLE_BASE_PATH),
                identifiers(properties, SUBMITTER_IDS),
                identifiers(properties, MODERATOR_IDS),
                zone(properties, TIME_ZONE));
    }

    /**
     * Legge UUID separati da virgole; una proprietà presente ma vuota produce un insieme vuoto.
     * Gli spazi esterni ai singoli UUID vengono rimossi e i duplicati confluiscono in un solo elemento.
     *
     * @param properties proprietà caricate dal file esterno
     * @param key chiave dell'elenco
     * @return insieme immutabile di identificativi
     * @throws IllegalStateException se la chiave manca, contiene un elemento vuoto tra separatori
     *         o contiene un valore che non è un UUID
     */
    private static Set<UUID> identifiers(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("missing configuration value: " + key);
        }
        if (value.isBlank()) {
            return Set.of();
        }
        Set<UUID> identifiers = new HashSet<>();
        for (String item : value.split(VALUE_SEPARATOR, -1)) {
            String identifier = item.strip();
            if (identifier.isEmpty()) {
                throw new IllegalStateException("empty identifier for " + key);
            }
            try {
                identifiers.add(UUID.fromString(identifier));
            } catch (IllegalArgumentException cause) {
                throw new IllegalStateException("invalid identifier for " + key, cause);
            }
        }
        return Set.copyOf(identifiers);
    }

    /**
     * Legge il fuso orario configurato.
     *
     * @param properties proprietà caricate dal file esterno
     * @param key chiave del fuso orario
     * @return fuso orario valido
     * @throws IllegalStateException se il valore manca o non identifica un fuso valido
     */
    private static ZoneId zone(Properties properties, String key) {
        try {
            return ZoneId.of(required(properties, key));
        } catch (DateTimeException cause) {
            throw new IllegalStateException("invalid time zone for " + key, cause);
        }
    }

    /**
     * Legge una stringa obbligatoria, rigettando valori assenti o vuoti.
     *
     * @param properties proprietà lette dal file di configurazione
     * @param key chiave da cercare
     * @return valore non vuoto associato alla chiave
     * @throws IllegalStateException se la chiave non è presente o il valore è vuoto
     */
    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("missing configuration value: " + key);
        }
        return value;
    }

    /**
     * Legge una chiave che deve essere presente e il cui valore può essere vuoto.
     *
     * @param properties proprietà lette dal file di configurazione
     * @param key chiave da cercare
     * @return valore associato alla chiave, privato degli spazi ai margini
     * @throws IllegalStateException se la chiave non è presente
     */
    private static String configured(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("missing configuration key: " + key);
        }
        return value.strip();
    }

    /**
     * Legge una chiave presente e interpreta il valore come elenco di stringhe separate da virgole.
     * Rimuove gli spazi ai margini, ignora gli elementi vuoti e restituisce un insieme senza duplicati.
     *
     * @param properties proprietà lette dal file di configurazione
     * @param key chiave da cercare
     * @return insieme immutabile dei valori non vuoti; è vuoto se il valore è vuoto o contiene solo separatori
     * @throws IllegalStateException se la chiave non è presente
     */
    private static Set<String> configuredSet(Properties properties, String key) {
        String value = configured(properties, key);
        Set<String> values = new LinkedHashSet<>();
        if (!value.isBlank()) {
            for (String item : value.split(VALUE_SEPARATOR)) {
                String trimmed = item.strip();
                if (!trimmed.isEmpty()) {
                    values.add(trimmed);
                }
            }
        }
        return Set.copyOf(values);
    }

    /**
     * Legge un valore obbligatorio e lo interpreta come intero lungo.
     *
     * @param properties proprietà lette dal file di configurazione
     * @param key chiave da cercare
     * @return valore intero associato alla chiave
     * @throws IllegalStateException se il valore è assente, vuoto o non è un intero lungo valido
     */
    private static long requiredLong(Properties properties, String key) {
        try {
            return Long.parseLong(required(properties, key));
        } catch (NumberFormatException cause) {
            throw new IllegalStateException("invalid integer for " + key, cause);
        }
    }

    /**
     * Legge un valore obbligatorio e lo interpreta come intero.
     *
     * @param properties proprietà lette dal file di configurazione
     * @param key chiave da cercare
     * @return valore intero associato alla chiave
     * @throws IllegalStateException se il valore è assente, vuoto o non è un intero valido
     */
    private static int requiredInt(Properties properties, String key) {
        try {
            return Integer.parseInt(required(properties, key));
        } catch (NumberFormatException cause) {
            throw new IllegalStateException("invalid integer for " + key, cause);
        }
    }

    /**
     * Legge un valore obbligatorio e lo interpreta come numero decimale.
     *
     * @param properties proprietà lette dal file di configurazione
     * @param key chiave da cercare
     * @return valore decimale associato alla chiave
     * @throws IllegalStateException se il valore è assente, vuoto o non è un decimale valido
     */
    private static double requiredDouble(Properties properties, String key) {
        try {
            return Double.parseDouble(required(properties, key));
        } catch (NumberFormatException cause) {
            throw new IllegalStateException("invalid decimal for " + key, cause);
        }
    }
}
