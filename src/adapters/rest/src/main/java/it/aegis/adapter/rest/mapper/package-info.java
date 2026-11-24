/**
 * Conversione esplicita tra il dominio e le rappresentazioni HTTP.
 *
 * <p>I mapper traducono i tipi del dominio nei DTO del livello REST senza
 * introdurre regole proprie. Il mapper di eccezioni produce invece la
 * rappresentazione uniforme dell'errore usando solo la classificazione
 * pubblica e il bundle localizzato. Il contratto JSON non dipende dalla
 * struttura interna dell'aggregato e dal suo stato.</p>
 */
package it.aegis.adapter.rest.mapper;
