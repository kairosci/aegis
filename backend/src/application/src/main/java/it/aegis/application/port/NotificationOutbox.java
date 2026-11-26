package it.aegis.application.port;

import java.util.List;

/**
 * Lettura e conferma delle intenzioni di notifica da parte del worker.
 * Un'intenzione resta in attesa finché il worker non ne registra la conferma.
 */
public interface NotificationOutbox {

    /**
     * Restituisce le intenzioni di notifica in attesa di consegna.
     *
     * @return elenco delle intenzioni non ancora confermate
     */
    List<NotificationIntent> pending();

    /**
     * Registra la conferma di un'intenzione già consegnata.
     *
     * @param intent intenzione da confermare
     */
    void acknowledge(NotificationIntent intent);
}
