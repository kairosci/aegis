/**
 * Rappresentazioni HTTP dei dati scambiati con i client REST.
 *
 * <p>I record di questo pacchetto costituiscono il contratto JSON: espongono
 * valori testuali o primitivi invece dei tipi del dominio. Le richieste
 * verificano i campi obbligatori al confine HTTP, il percorso restituisce un
 * DTO esplicito e le risposte di errore usano una forma uniforme con codice e
 * messaggio localizzato. Non vengono esposte informazioni non autorizzate.</p>
 */
package it.aegis.adapter.rest.dto;
