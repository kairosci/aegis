package it.aegis.adapter.storage;

import it.aegis.config.MessageSource;
import java.util.Locale;
import java.util.Objects;

/**
 * Errore tipizzato del confine di integrazione con lo storage privato.
 *
 * <p>Il messaggio è sempre risolto dal bundle configurato a partire da una
 * chiave logica. L'eccezione non include il nome del file, la credenziale o
 * altri valori provenienti dalla richiesta.</p>
 */
public final class StorageAdapterException extends RuntimeException {

    /** Identificatore di serializzazione della classe di errore. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea l'errore con una causa sottostante.
     *
     * @param messageKey chiave logica del messaggio nel bundle degli adattatori
     * @param messages sorgente dei messaggi configurata
     * @param cause causa di trasporto o di validazione
     */
    public StorageAdapterException(String messageKey, MessageSource messages, Throwable cause) {
        super(message(messageKey, messages), cause);
    }

    /**
     * Crea l'errore senza una causa sottostante.
     *
     * @param messageKey chiave logica del messaggio nel bundle degli adattatori
     * @param messages sorgente dei messaggi configurata
     */
    public StorageAdapterException(String messageKey, MessageSource messages) {
        super(message(messageKey, messages));
    }

    /**
     * Risolve la chiave logica nel bundle configurato.
     *
     * @param messageKey chiave logica del messaggio
     * @param messages sorgente dei messaggi configurata
     * @return testo localizzato associato alla chiave
     */
    private static String message(String messageKey, MessageSource messages) {
        Objects.requireNonNull(messageKey, "messageKey");
        Objects.requireNonNull(messages, "messages");
        return messages.get(messageKey, Locale.ROOT);
    }
}
