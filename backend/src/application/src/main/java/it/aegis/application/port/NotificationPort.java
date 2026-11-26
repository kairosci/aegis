package it.aegis.application.port;

/**
 * Scrittura dell'intenzione di notifica nella stessa unità di lavoro del caso d'uso.
 * La scrittura deve partecipare alla transazione che modifica l'aggregato, così
 * che lo stato e l'intenzione cambino insieme.
 */
public interface NotificationPort {

    /**
     * Registra un'intenzione di notifica da consegnare dopo il commit.
     *
     * @param intent intenzione da persistere
     */
    void enqueue(NotificationIntent intent);
}
