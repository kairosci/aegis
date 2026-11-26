/**
 * Contratti applicativi delle porte del layer application.
 *
 * <p>Le interfacce e i tipi di questo package descrivono le operazioni di
 * persistenza, le politiche di autorizzazione, la generazione del codice di
 * tracking e la consegna delle notifiche. Le implementazioni risiedono negli
 * adapter: il layer application dipende solo da questi contratti e da tipi di
 * dominio, senza dipendere da API di infrastruttura.
 *
 * <p>Le porte di notifica distinguono la scrittura dell'intenzione nella stessa
 * unità di lavoro del caso d'uso dalla consegna da parte del worker, che non
 * garantisce consegna esattamente una volta.
 *
 * <p>Le porte di integrazione descrivono snapshot di fermate e riferimenti a
 * oggetti privati. I tipi restituiti sono immutabili e non trasportano
 * affermazioni di accessibilità, così una fonte mancante o parziale resta
 * distinta da un dato verificato.
 */
package it.aegis.application.port;
