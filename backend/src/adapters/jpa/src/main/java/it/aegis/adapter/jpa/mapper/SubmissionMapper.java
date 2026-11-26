package it.aegis.adapter.jpa.mapper;

import it.aegis.adapter.jpa.entity.ReportSubmissionEntity;
import it.aegis.adapter.jpa.entity.SubmissionReservationId;
import it.aegis.adapter.jpa.entity.SubmissionState;
import it.aegis.application.port.Fingerprint;
import it.aegis.application.port.ReservationOutcome;
import it.aegis.application.port.SubmissionKey;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.TrackingCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Conversione tra i tipi della porta di prenotazione e la riga persistente.
 *
 * <p>Il digest della chiave di invio è calcolato con l'algoritmo di digest già
 * dichiarato dalla piattaforma. L'impronta del contenuto è invece conservata
 * senza ricalcolarla, perché proviene dalla validazione applicativa.
 */
public final class SubmissionMapper {

    /** Impedisce l'istanziazione di una classe che espone solo metodi statici. */
    private SubmissionMapper() {
    }

    /**
     * Crea la riga di una prenotazione in corso.
     *
     * @param key chiave di idempotenza del client
     * @param fingerprint impronta canonica del contenuto
     * @param authorId autore autenticato della segnalazione
     * @return riga da persistere con stato in corso
     */
    public static ReportSubmissionEntity toEntity(
            SubmissionKey key, Fingerprint fingerprint, UserId authorId) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(fingerprint);
        Objects.requireNonNull(authorId);
        return new ReportSubmissionEntity(
                new SubmissionReservationId(authorId.value(), digest(key.value())),
                fingerprint.sha256Hex());
    }

    /**
     * Restituisce l'esito della porta associato allo stato della riga.
     *
     * @param row riga della prenotazione
     * @return esito completato con il codice, oppure in corso
     * @throws IllegalStateException se la riga non ha uno stato riconosciuto o un codice atteso
     */
    public static ReservationOutcome toOutcome(ReportSubmissionEntity row) {
        if (row.status() == SubmissionState.COMPLETED) {
            if (row.trackingCode() == null) {
                throw new IllegalStateException();
            }
            return new ReservationOutcome.Completed(new TrackingCode(row.trackingCode()));
        }
        if (row.status() == SubmissionState.PENDING) {
            return new ReservationOutcome.InProgress();
        }
        throw new IllegalStateException();
    }

    /**
     * Calcola il digest esadecimale della chiave di invio.
     *
     * @param value valore della chiave di idempotenza
     * @return digest esadecimale da usare nella chiave della prenotazione
     * @throws IllegalStateException se la piattaforma non fornisce l'algoritmo richiesto
     */
    public static String digest(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance(Fingerprint.algorithm())
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException cause) {
            throw new IllegalStateException(cause);
        }
    }
}
