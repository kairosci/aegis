package it.aegis.adapter.inmemory;

import it.aegis.application.port.NotificationChannel;
import it.aegis.application.port.NotificationIntent;
import java.util.ArrayList;
import java.util.List;

/** Canale in memoria che registra le consegne senza contatti esterni. */
public final class RecordingNotificationChannel implements NotificationChannel {

    private final List<NotificationIntent> delivered = new ArrayList<>();

    @Override
    public void send(NotificationIntent intent) {
        delivered.add(intent);
    }

    public List<NotificationIntent> delivered() {
        return List.copyOf(delivered);
    }
}
