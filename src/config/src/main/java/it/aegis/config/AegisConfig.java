package it.aegis.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

/**
 * Configurazione tipizzata. Ogni valore deriva dal file esterno e nessuno ha un
 * default silenzioso. La directory dati e il percorso del bundle sono inclusi
 * perché anche i testi dell'interfaccia restino fuori dal codice.
 *
 * <p>Il costruttore compatto verifica la presenza dei valori obbligatori e la
 * coerenza delle grandezze numeriche, così una configurazione errata viene
 * individuata al caricamento invece che durante l'uso.
 *
 * @param freshnessWindow finestra di freschezza delle evidenze di accessibilità, positiva
 * @param maxAttachmentBytes dimensione massima di un allegato in byte, positiva
 * @param trackingEntropyBits entropia del codice di tracking in bit, multiplo positivo di {@link Byte#SIZE}
 * @param trackingAlphabet alfabeto ordinato usato per il codice di tracking, non vuoto
 * @param earthRadiusMetres raggio terrestre usato dal calcolo geografico, positivo e finito
 * @param dataDirectory directory esterna che contiene i dati applicativi
 * @param messageBundleBasePath percorso del bundle messaggi relativo alla directory dati, non vuoto
 */
public record AegisConfig(
        Duration freshnessWindow,
        long maxAttachmentBytes,
        int trackingEntropyBits,
        String trackingAlphabet,
        double earthRadiusMetres,
        Path dataDirectory,
        String messageBundleBasePath) {

    /**
     * Verifica che i valori obbligatori siano presenti e rispettino i vincoli attesi.
     *
     * @throws NullPointerException se una componente obbligatoria è {@code null}
     * @throws IllegalArgumentException se la finestra, la dimensione massima, l'entropia,
     *         l'alfabeto, il percorso del bundle o il raggio terrestre non sono validi
     */
    public AegisConfig {
        Objects.requireNonNull(freshnessWindow, "freshnessWindow");
        Objects.requireNonNull(trackingAlphabet, "trackingAlphabet");
        Objects.requireNonNull(dataDirectory, "dataDirectory");
        Objects.requireNonNull(messageBundleBasePath, "messageBundleBasePath");
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
