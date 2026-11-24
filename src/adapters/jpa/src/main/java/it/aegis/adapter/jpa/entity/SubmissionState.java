package it.aegis.adapter.jpa.entity;

/**
 * Stati della prenotazione durevole di un invio.
 *
 * <p>I nomi sono persistiti come token testuali e corrispondono agli stati descritti
 * dallo schema relazionale per la prenotazione prima e dopo il completamento.
 */
public enum SubmissionState {

    /** Prenotazione registrata prima degli effetti esterni e ancora in corso. */
    PENDING,

    /** Prenotazione completata con il codice di tracking della segnalazione salvata. */
    COMPLETED
}
