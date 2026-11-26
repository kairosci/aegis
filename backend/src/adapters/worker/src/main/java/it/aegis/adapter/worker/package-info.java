/**
 * Adattatore per l'elaborazione asincrona delle notifiche.
 *
 * <p>Il worker legge le intenzioni di notifica accumulate nell'outbox dopo il
 * commit e le consegna al canale esterno. La consegna non garantisce
 * l'esattamente una volta, quindi la conferma avviene solo dopo l'invio.</p>
 */
package it.aegis.adapter.worker;
