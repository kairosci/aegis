package it.aegis.application.port;

/** Canale esterno di consegna. Non garantisce consegna esattamente una volta. */
public interface NotificationChannel {

    void send(NotificationIntent intent);
}
