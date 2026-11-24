package it.aegis.application.port;

import java.util.List;

/** Lettura e conferma delle intenzioni di notifica da parte del worker. */
public interface NotificationOutbox {

    List<NotificationIntent> pending();

    void acknowledge(NotificationIntent intent);
}
