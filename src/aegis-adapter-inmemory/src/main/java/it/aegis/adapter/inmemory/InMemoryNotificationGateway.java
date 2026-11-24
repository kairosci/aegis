package it.aegis.adapter.inmemory;

import it.aegis.application.port.NotificationIntent;
import it.aegis.application.port.NotificationOutbox;
import it.aegis.application.port.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/** Coda in memoria delle intenzioni di notifica. */
public final class InMemoryNotificationGateway implements NotificationPort, NotificationOutbox {

    private final List<NotificationIntent> pending = new ArrayList<>();

    @Override
    public void enqueue(NotificationIntent intent) {
        pending.add(intent);
    }

    @Override
    public List<NotificationIntent> pending() {
        return List.copyOf(pending);
    }

    @Override
    public void acknowledge(NotificationIntent intent) {
        pending.remove(intent);
    }
}
