/**
 * Casi d'uso dell'applicazione e relativi comandi, query e risultati pubblici.
 *
 * <p>Le classi di questo package orchestrano le porte definite in
 * {@link it.aegis.application.port} e le regole di dominio: verificano le
 * autorizzazioni lato server, risolvono gli identificativi, applicano le
 * transizioni dell'aggregato e producono i risultati delle consultazioni.
 *
 * <p>I comandi e le query validano gli input al confine dell'applicazione;
 * la prenotazione durevole dell'invio e la scrittura dell'intenzione di
 * notifica precedono gli effetti esterni e partecipano alla stessa unità di
 * lavoro della modifica dello stato.
 */
package it.aegis.application.usecase;
