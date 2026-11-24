package it.aegis.config;

import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Configurazione tipizzata letta dalla directory dati esterna al codice.
 *
 * <p>Ogni valore deriva dal file esterno e nessuno ha un default silenzioso.
 * Le identità autorizzate sono insiemi vuoti quando le chiavi corrispondenti
 * sono vuote, così una configurazione iniziale non concede permessi.
 *
 * <p>Il costruttore pubblico verifica la presenza dei valori obbligatori e la
 * coerenza delle grandezze numeriche. Il costruttore protetto esiste solo per
 * il proxy CDI richiesto dallo scope applicativo.
 */
public class AegisConfig {

    /** Finestra di freschezza delle evidenze di accessibilità. */
    private final Duration freshnessWindow;

    /** Dimensione massima di un allegato in byte. */
    private final long maxAttachmentBytes;

    /** Entropia del codice di tracking in bit. */
    private final int trackingEntropyBits;

    /** Alfabeto ordinato usato per il codice di tracking. */
    private final String trackingAlphabet;

    /** Raggio terrestre usato dal calcolo geografico. */
    private final double earthRadiusMetres;

    /** Directory esterna che contiene i dati applicativi. */
    private final Path dataDirectory;

    /** Percorso del bundle messaggi relativo alla directory dati. */
    private final String messageBundleBasePath;

    /** Identificativi degli utenti autorizzati a inviare segnalazioni. */
    private final Set<UUID> submitterIds;

    /** Identificativi degli utenti autorizzati a moderare le segnalazioni. */
    private final Set<UUID> moderatorIds;

    /** Fuso orario usato per datare eventi e segnalazioni. */
    private final ZoneId timeZone;

    /**
     * Costruttore protetto riservato alla creazione del proxy CDI.
     *
     * <p>Le istanze usate dall'applicazione sono costruite dal caricamento
     * della configurazione, che verifica i valori e li rende immutabili.
     */
    protected AegisConfig() {
        this.freshnessWindow = null;
        this.maxAttachmentBytes = 0L;
        this.trackingEntropyBits = 0;
        this.trackingAlphabet = null;
        this.earthRadiusMetres = 0.0;
        this.dataDirectory = null;
        this.messageBundleBasePath = null;
        this.submitterIds = Set.of();
        this.moderatorIds = Set.of();
        this.timeZone = null;
    }

    /**
     * Crea una configurazione verificata a partire dai valori letti dal file esterno.
     *
     * @param freshnessWindow finestra di freschezza delle evidenze di accessibilità, positiva
     * @param maxAttachmentBytes dimensione massima di un allegato in byte, positiva
     * @param trackingEntropyBits entropia del codice di tracking in bit, multiplo positivo di {@link Byte#SIZE}
     * @param trackingAlphabet alfabeto ordinato usato per il codice di tracking, non vuoto
     * @param earthRadiusMetres raggio terrestre usato dal calcolo geografico, positivo e finito
     * @param dataDirectory directory esterna che contiene i dati applicativi
     * @param messageBundleBasePath percorso del bundle messaggi relativo alla directory dati, non vuoto
     * @param submitterIds identificativi autorizzati all'invio, eventualmente vuoti
     * @param moderatorIds identificativi autorizzati alla moderazione, eventualmente vuoti
     * @param timeZone fuso orario usato per datare eventi e segnalazioni
     * @throws NullPointerException se una componente obbligatoria è {@code null}
     * @throws IllegalArgumentException se la finestra, la dimensione massima, l'entropia,
     *         l'alfabeto, il percorso del bundle o il raggio terrestre non sono validi
     */
    public AegisConfig(
            Duration freshnessWindow,
            long maxAttachmentBytes,
            int trackingEntropyBits,
            String trackingAlphabet,
            double earthRadiusMetres,
            Path dataDirectory,
            String messageBundleBasePath,
            Set<UUID> submitterIds,
            Set<UUID> moderatorIds,
            ZoneId timeZone) {
        this.freshnessWindow = Objects.requireNonNull(freshnessWindow, "freshnessWindow");
        this.trackingAlphabet = Objects.requireNonNull(trackingAlphabet, "trackingAlphabet");
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
        this.messageBundleBasePath = Objects.requireNonNull(messageBundleBasePath, "messageBundleBasePath");
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
        if (!Double.isFinite(earthRadiusMetres) || earthRadiusMetres <= 0.0) {
            throw new IllegalArgumentException("earthRadiusMetres must be positive");
        }
    }

    /**
     * Restituisce la finestra di freschezza delle evidenze.
     *
     * @return durata configurata della finestra di freschezza
     */
    public Duration freshnessWindow() {
        return freshnessWindow;
    }

    /**
     * Restituisce la dimensione massima di un allegato.
     *
     * @return numero massimo di byte di un allegato
     */
    public long maxAttachmentBytes() {
        return maxAttachmentBytes;
    }

    /**
     * Restituisce l'entropia richiesta per il codice di tracking.
     *
     * @return entropia configurata in bit
     */
    public int trackingEntropyBits() {
        return trackingEntropyBits;
    }

    /**
     * Restituisce l'alfabeto ordinato del codice di tracking.
     *
     * @return alfabeto configurato
     */
    public String trackingAlphabet() {
        return trackingAlphabet;
    }

    /**
     * Restituisce il raggio terrestre usato nei calcoli geografici.
     *
     * @return raggio configurato in metri
     */
    public double earthRadiusMetres() {
        return earthRadiusMetres;
    }

    /**
     * Restituisce la directory esterna dei dati applicativi.
     *
     * @return percorso della directory dati
     */
    public Path dataDirectory() {
        return dataDirectory;
    }

    /**
     * Restituisce il percorso del bundle dei messaggi.
     *
     * @return percorso relativo alla directory dati
     */
    public String messageBundleBasePath() {
        return messageBundleBasePath;
    }

    /**
     * Restituisce gli identificativi autorizzati a inviare segnalazioni.
     *
     * @return insieme non modificabile di identificativi, eventualmente vuoto
     */
    public Set<UUID> submitterIds() {
        return submitterIds;
    }

    /**
     * Restituisce gli identificativi autorizzati a moderare segnalazioni.
     *
     * @return insieme non modificabile di identificativi, eventualmente vuoto
     */
    public Set<UUID> moderatorIds() {
        return moderatorIds;
    }

    /**
     * Restituisce il fuso orario configurato.
     *
     * @return fuso orario usato per datare eventi e segnalazioni
     */
    public ZoneId timeZone() {
        return timeZone;
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
}
