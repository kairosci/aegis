package it.aegis.application.port;

/**
 * Porta verso lo storage privato degli allegati.
 *
 * <p>La porta separa la regola applicativa dal servizio di object storage.
 * L'oggetto scritto resta privato e il riferimento restituito non espone il
 * nome del file al client. Gli errori di trasporto o di contratto sono
 * segnalati dall'adattatore senza modificare lo stato dell'applicazione.</p>
 */
public interface StoragePort {

    /**
     * Scrive il contenuto in un oggetto privato e restituisce il suo riferimento.
     *
     * @param content byte del contenuto da conservare
     * @param fileName nome dichiarato dal client, usato solo per determinare il tipo di contenuto
     * @param submissionId identificativo della prenotazione che autorizza la scrittura
     * @return riferimento all'oggetto privato scritto
     */
    StorageReference stage(byte[] content, String fileName, String submissionId);

    /**
     * Rimuove un oggetto precedentemente scritto.
     *
     * @param reference riferimento restituito da {@link #stage(byte[], String, String)}
     */
    void delete(StorageReference reference);
}
