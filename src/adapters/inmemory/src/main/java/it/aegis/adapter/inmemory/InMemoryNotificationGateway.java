package it.aegis.adapter.inmemory;

import it.aegis.application.port.NotificationIntent;
import it.aegis.application.port.NotificationOutbox;
import it.aegis.application.port.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Coda in memoria delle intenzioni di notifica.
 *
 * <p>Implementa sia la porta di scrittura sia l'outbox consultato dal worker: le
 * intenzioni restano nel processo e non producono effetti esterni. La struttura
 * non è pensata per accessi concorrenti da più thread.
 */
public final class InMemoryNotificationGateway implements NotificationPort, NotificationOutbox {

    /** Intenzioni in attesa di consegna, in ordine di accodamento. */
    private final List<NotificationIntent> pending = new ArrayList<>();

    /** Crea una coda vuota di intenzioni in attesa di consegna. */
    public InMemoryNotificationGateway() {
    }

    /**
     * Accoda un'intenzione in attesa di consegna.
     *
     * @param intent intenzione da accodare
     */
    @Override
    public void enqueue(NotificationIntent intent) {
        pending.add(intent);
    }

    /**
     * Restituisce una copia non modificabile delle intenzioni in attesa.
     *
     * @return istantanea delle intenzioni accodate
     */
    @Override
    public List<NotificationIntent> pending() {
        return List.copyOf(pending);
    }

    /**
     * Rimuove un'intenzione già consegnata dalla coda.
     *
     * @param intent intenzione da rimuovere
     */
    @Override
    public void acknowledge(NotificationIntent intent) {
        pending.remove(intent);
    }
}
