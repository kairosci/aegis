package it.aegis.adapter.worker;

import it.aegis.application.port.NotificationChannel;
import it.aegis.application.port.NotificationIntent;
import it.aegis.application.port.NotificationOutbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Objects;

/**
 * Elabora l'outbox dopo il commit. La consegna non è esattamente una volta.
 *
 * <p>Per ogni intenzione in attesa il worker invia prima il messaggio al canale
 * esterno e poi registra la conferma nell'outbox, contando le intenzioni
 * elaborate. Lo scope applicativo permette l'iniezione delle porte senza
 * richiedere un contesto di richiesta.</p>
 */
@ApplicationScoped
public class OutboxNotificationWorker {

    /** Outbox da cui leggere e confermare le intenzioni in attesa. */
    private final NotificationOutbox outbox;

    /** Canale esterno che consegna le notifiche. */
    private final NotificationChannel channel;

    /**
     * Crea il worker con l'outbox e il canale di consegna.
     *
     * @param outbox outbox da cui leggere e confermare le intenzioni in attesa
     * @param channel canale esterno che consegna le notifiche
     * @throws NullPointerException se {@code outbox} o {@code channel} sono null
     */
    @Inject
    public OutboxNotificationWorker(NotificationOutbox outbox, NotificationChannel channel) {
        this.outbox = Objects.requireNonNull(outbox, "outbox");
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    /**
     * Invia le intenzioni in attesa e ne registra la conferma.
     *
     * <p>La conferma segue la consegna: se il canale solleva un'eccezione,
     * l'elaborazione si interrompe e l'intenzione resta in attesa di un nuovo
     * tentativo. Il conteggio restituito comprende soltanto le intenzioni
     * inviate e confermate con successo.</p>
     *
     * @return numero di intenzioni elaborate in questa esecuzione
     */
    public int processPending() {
        int processed = 0;
        for (NotificationIntent intent : outbox.pending()) {
            channel.send(intent);
            outbox.acknowledge(intent);
            processed++;
        }
        return processed;
    }
}
