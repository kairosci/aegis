package it.aegis.adapter.worker;

import it.aegis.application.port.NotificationChannel;
import it.aegis.application.port.NotificationIntent;
import it.aegis.application.port.NotificationOutbox;
import jakarta.inject.Inject;
import java.util.Objects;

/** Elabora l'outbox dopo il commit. La consegna non è esattamente una volta. */
public final class OutboxNotificationWorker {

    private final NotificationOutbox outbox;
    private final NotificationChannel channel;

    @Inject
    public OutboxNotificationWorker(NotificationOutbox outbox, NotificationChannel channel) {
        this.outbox = Objects.requireNonNull(outbox, "outbox");
        this.channel = Objects.requireNonNull(channel, "channel");
    }

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
