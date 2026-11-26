package it.aegis.application.port;

/**
 * Canale esterno di consegna. Non garantisce consegna esattamente una volta.
 * I chiamanti devono quindi trattare la consegna come eventuale e confermare
 * esplicitamente le intenzioni gestite tramite {@link NotificationOutbox}.
 */
public interface NotificationChannel {

    /**
     * Invia un'intenzione di notifica al canale configurato.
     *
     * @param intent intenzione da consegnare, con destinatario, segnalazione e stato
     */
    void send(NotificationIntent intent);
}
