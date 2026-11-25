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
 * Sorgente dei testi dell'interfaccia letta dalla directory dati esterna al
 * codice. Il percorso del bundle arriva dalla configurazione, quindi il codice
 * non contiene né nomi di file né testi localizzati.
 *
 * <p>Il bundle è caricato dalla directory dati e la ricerca segue le regole di
 * fallback di {@link ResourceBundle} a partire dalla lingua richiesta.
 *
 * @param dataDirectory directory esterna che contiene il bundle
 * @param bundleBasePath percorso del bundle relativo alla directory dati, senza suffisso e lingua
 */
public record MessageSource(Path dataDirectory, String bundleBasePath) {

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
     * Crea una sorgente per un percorso di bundle esplicito, risolvendo la
     * directory dati tramite la configurazione esterna. Il metodo consente di
     * tenere separati il bundle della vista e quello degli adattatori senza
     * ripetere percorsi di file nel codice.
     *
     * @param basePath percorso del bundle relativo alla directory dati, senza suffisso e lingua
     * @return sorgente che legge il bundle richiesto dalla directory dati configurata
     * @throws NullPointerException se {@code basePath} è {@code null}
     * @throws IllegalArgumentException se il percorso è vuoto
     * @throws IllegalStateException se la configurazione esterna non è disponibile
     */
    public static MessageSource at(String basePath) {
        Objects.requireNonNull(basePath, "basePath");
        if (basePath.isBlank()) {
            throw new IllegalArgumentException("basePath must not be blank");
        }
        return new MessageSource(ConfigLoader.load().dataDirectory(), basePath);
    }

    /**
     * Crea una sorgente per il percorso indicato usando una configurazione già caricata.
     *
     * @param config configurazione applicativa tipizzata
     * @param basePath percorso del bundle relativo alla directory dati
     * @return sorgente che legge il bundle dalla directory configurata
     * @throws NullPointerException se la configurazione o il percorso è {@code null}
     */
    public static MessageSource at(AegisConfig config, String basePath) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(basePath, "basePath");
        return new MessageSource(config.dataDirectory(), basePath);
    }

    /**
     * Compattatore del record. Verifica che la directory e il percorso del bundle siano presenti.
     *
     * @throws NullPointerException se la directory o il percorso del bundle è {@code null}
     * @throws IllegalArgumentException se il percorso del bundle è vuoto
     */
    public MessageSource {
        Objects.requireNonNull(dataDirectory, "dataDirectory");
        Objects.requireNonNull(bundleBasePath, "bundleBasePath");
        if (bundleBasePath.isBlank()) {
            throw new IllegalArgumentException("bundleBasePath must not be blank");
        }
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
