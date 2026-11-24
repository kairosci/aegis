package it.aegis.config;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Sorgente dei testi dell'interfaccia letta dalla directory dati esterna al codice.
 *
 * <p>Il percorso del bundle arriva dalla configurazione, quindi il codice non
 * contiene né nomi di file né testi localizzati. Il bundle è caricato dalla
 * directory dati e la ricerca segue le regole di fallback di
 * {@link ResourceBundle} a partire dalla lingua richiesta.
 *
 * <p>Il costruttore protetto esiste solo per il proxy CDI richiesto dallo
 * scope applicativo. Le istanze usate dall'applicazione sono create dalla
 * configurazione verificata.
 */
public class MessageSource {

    /** Directory esterna che contiene il bundle. */
    private final Path dataDirectory;

    /** Percorso del bundle relativo alla directory dati, senza suffisso e lingua. */
    private final String bundleBasePath;

    /**
     * Costruttore protetto riservato alla creazione del proxy CDI.
     *
     * <p>Le istanze usate dall'applicazione sono create dalla configurazione
     * e verificano i valori obbligatori.
     */
    protected MessageSource() {
        this.dataDirectory = null;
        this.bundleBasePath = null;
    }

    /**
     * Crea la sorgente dei messaggi a partire dalla configurazione caricata.
     *
     * @param config configurazione applicativa tipizzata
     * @return sorgente che legge i messaggi dalla directory dati configurata
     */
    public static MessageSource from(AegisConfig config) {
        Objects.requireNonNull(config, "config");
        return new MessageSource(config.dataDirectory(), config.messageBundleBasePath());
    }

    /**
     * Crea la sorgente dei messaggi verificando directory e percorso del bundle.
     *
     * @param dataDirectory directory esterna che contiene il bundle
     * @param bundleBasePath percorso del bundle relativo alla directory dati, senza suffisso e lingua
     * @throws NullPointerException se la directory o il percorso del bundle è {@code null}
     * @throws IllegalArgumentException se il percorso del bundle è vuoto
     */
    public MessageSource(Path dataDirectory, String bundleBasePath) {
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
        this.bundleBasePath = Objects.requireNonNull(bundleBasePath, "bundleBasePath");
        if (bundleBasePath.isBlank()) {
            throw new IllegalArgumentException("bundleBasePath must not be blank");
        }
    }

    /**
     * Restituisce la directory esterna che contiene il bundle.
     *
     * @return percorso della directory dati
     */
    public Path dataDirectory() {
        return dataDirectory;
    }

    /**
     * Restituisce il percorso del bundle relativo alla directory dati.
     *
     * @return percorso di base del bundle senza suffisso e lingua
     */
    public String bundleBasePath() {
        return bundleBasePath;
    }

    /**
     * Restituisce un messaggio localizzato. La ricerca applica le regole di
     * fallback di {@link ResourceBundle} partendo dalla lingua richiesta.
     *
     * @param key chiave logica del messaggio
     * @param locale lingua richiesta dalla vista
     * @return testo del messaggio letto dalla directory dati
     * @throws IllegalStateException se il bundle o la chiave non sono disponibili
     */
    public String get(String key, Locale locale) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(locale, "locale");
        String baseName = bundleBasePath.replace('/', '.');
        try (URLClassLoader loader = new URLClassLoader(new URL[] {dataDirectory.toUri().toURL()}, null)) {
            return ResourceBundle.getBundle(baseName, locale, loader).getString(key);
        } catch (IOException | MissingResourceException cause) {
            throw new IllegalStateException("cannot read message " + key + " from " + bundleBasePath, cause);
        }
    }
}
