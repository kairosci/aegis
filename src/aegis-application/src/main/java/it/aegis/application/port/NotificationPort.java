package it.aegis.application.port;

/** Scrittura dell'intenzione di notifica nella stessa unità di lavoro del caso d'uso. */
public interface NotificationPort {

    void enqueue(NotificationIntent intent);
}
