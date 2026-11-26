/**
 * Risorse JAX-RS che espongono i casi d'uso tramite HTTP.
 *
 * <p>Ogni risorsa usa lo scope CDI di richiesta, riceve i casi d'uso per iniezione, verifica
 * i dati di input necessari e traduce gli esiti in risposte HTTP. Gli errori di
 * confine seguono il contratto uniforme del pacchetto degli errori. Le regole
 * di dominio non vengono duplicate nelle risorse.</p>
 */
package it.aegis.adapter.rest.resource;
