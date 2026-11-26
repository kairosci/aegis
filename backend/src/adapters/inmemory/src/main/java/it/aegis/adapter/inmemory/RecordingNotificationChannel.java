package it.aegis.adapter.inmemory;

import it.aegis.application.port.NotificationChannel;
import it.aegis.application.port.NotificationIntent;
import java.util.ArrayList;
import java.util.List;

/**
 * Canale in memoria che registra le consegne senza contatti esterni.
 *
 * <p>Consente di osservare le intenzioni inviate durante lo sviluppo e le verifiche,
 * senza recapitarle a un servizio reale.
 */
public final class RecordingNotificationChannel implements NotificationChannel {

    /** Intenzioni consegnate, in ordine di invio. */
    private final List<NotificationIntent> delivered = new ArrayList<>();

    /** Crea un canale vuoto che registra le consegne in memoria. */
    public RecordingNotificationChannel() {
    }

    /**
     * Registra una consegna senza effettuarla all'esterno.
     *
     * @param intent intenzione da registrare
     */
    @Override
    public void send(NotificationIntent intent) {
        delivered.add(intent);
    }

    /**
     * Restituisce una copia non modificabile delle consegne registrate.
     *
     * @return istantanea delle intenzioni inviate al canale
     */
    public List<NotificationIntent> delivered() {
        return List.copyOf(delivered);
    }
}
