package it.aegis.adapter.routing;

import it.aegis.config.MessageSource;
import java.util.Locale;
import java.util.Objects;

/**
 * Errore tipizzato del confine di integrazione con il provider di routing.
 *
 * <p>Il testo non è costruito nell'adattatore: l'eccezione riceve una chiave
 * logica e la risolve nel bundle configurato. Nessun dettaglio della richiesta
 * o della risposta viene incluso nel messaggio.</p>
 */
public final class RoutingAdapterException extends RuntimeException {

    /** Identificatore di serializzazione della classe di errore. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea l'errore con una causa sottostante.
     *
     * @param messageKey chiave logica del messaggio nel bundle degli adattatori
     * @param messages sorgente dei messaggi configurata
     * @param cause causa di trasporto o di analisi della risposta
     */
    public RoutingAdapterException(String messageKey, MessageSource messages, Throwable cause) {
        super(message(messageKey, messages), cause);
    }

    /**
     * Crea l'errore senza una causa sottostante.
     *
     * @param messageKey chiave logica del messaggio nel bundle degli adattatori
     * @param messages sorgente dei messaggi configurata
     */
    public RoutingAdapterException(String messageKey, MessageSource messages) {
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
