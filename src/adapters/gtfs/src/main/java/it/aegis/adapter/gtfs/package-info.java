/**
 * Adattatore per fonti di trasporto in formato GTFS.
 *
 * <p>Questo package legge il file delle fermate dalla directory esterna
 * configurata usando solo il JDK. L'intestazione viene verificata, le righe
 * malformate sono scartate e le coordinate sono convalidate prima di entrare
 * nello snapshot. Nessun dato mancante viene trasformato in un'affermazione
 * di accessibilità.</p>
 */
package it.aegis.adapter.gtfs;
