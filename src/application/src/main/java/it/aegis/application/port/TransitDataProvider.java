package it.aegis.application.port;

/**
 * Porta verso una fonte di dati di trasporto.
 *
 * <p>I casi d'uso ricevono uno snapshot immutabile di fermate e restano
 * indipendenti dal formato della fonte. L'assenza della fonte o dati malformati
 * non producono affermazioni di accessibilità.</p>
 */
public interface TransitDataProvider {

    /**
     * Restituisce lo snapshot delle fermate disponibili nella fonte.
     *
     * @return snapshot immutabile, vuoto quando la fonte non è disponibile o valida
     */
    TransitStopSnapshot stops();
}
