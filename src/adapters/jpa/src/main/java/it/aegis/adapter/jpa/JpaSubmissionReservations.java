package it.aegis.adapter.jpa;

import it.aegis.adapter.jpa.entity.ReportSubmissionEntity;
import it.aegis.adapter.jpa.mapper.SubmissionMapper;
import it.aegis.application.port.Fingerprint;
import it.aegis.application.port.ReservationOutcome;
import it.aegis.application.port.SubmissionKey;
import it.aegis.application.port.SubmissionReservationPort;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.TrackingCode;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Objects;

/**
 * Implementazione JPA della prenotazione durevole degli invii.
 *
 * <p>L'unicità della chiave di invio nello scope dell'account è demandata al
 * vincolo della tabella. La prenotazione distingue un nuovo invio da un retry in
 * corso, completato o in conflitto con un contenuto diverso. Il comportamento
 * transazionale resta da collegare nell'assemblaggio applicativo.
 */
public final class JpaSubmissionReservations implements SubmissionReservationPort {

    /** Interrogazione della prenotazione con una data chiave. */
    private static final String RESERVATION_BY_DIGEST =
            "select reservation from ReportSubmissionEntity reservation where reservation.id.keyDigest = :digest";

    /** Gestore della persistenza iniettato dal contenitore. */
    private final EntityManager entityManager;

    /**
     * Crea l'adattatore con il gestore della persistenza iniettato.
     *
     * @param entityManager gestore della persistenza del contenitore
     */
    @Inject
    public JpaSubmissionReservations(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    /**
     * Registra una nuova prenotazione o restituisce lo stato di una già esistente.
     *
     * @param key chiave di idempotenza fornita dal client
     * @param fingerprint impronta canonica del contenuto dell'invio
     * @param authorId autore autenticato della segnalazione
     * @return esito che determina se proseguire o ripetere l'invio
     */
    @Override
    public ReservationOutcome reserve(
            SubmissionKey key, Fingerprint fingerprint, UserId authorId) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(fingerprint);
        Objects.requireNonNull(authorId);
        String digest = SubmissionMapper.digest(key.value());
        ReportSubmissionEntity sameKey = findByDigest(digest);
        if (sameKey == null) {
            try {
                entityManager.persist(SubmissionMapper.toEntity(key, fingerprint, authorId));
                entityManager.flush();
            } catch (EntityExistsException cause) {
                ReportSubmissionEntity concurrent = findByDigest(digest);
                if (concurrent != null) {
                    return outcomeOf(concurrent);
                }
                throw cause;
            }
            return new ReservationOutcome.New();
        }
        if (!sameKey.id().scope().equals(authorId.value())
                || !sameKey.fingerprint().equals(fingerprint.sha256Hex())) {
            return new ReservationOutcome.Conflict();
        }
        return outcomeOf(sameKey);
    }

    /**
     * Collega il codice di tracking a una prenotazione già registrata.
     *
     * @param key chiave di idempotenza della prenotazione da completare
     * @param code codice di tracking assegnato alla segnalazione salvata
     * @throws IllegalStateException se la prenotazione non esiste
     */
    @Override
    public void complete(SubmissionKey key, TrackingCode code) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(code);
        ReportSubmissionEntity row = findByDigest(SubmissionMapper.digest(key.value()));
        if (row == null) {
            throw new IllegalStateException();
        }
        row.markCompleted(code.value());
        entityManager.merge(row);
    }

    /**
     * Cerca una prenotazione dal digest della chiave, bloccando la riga in scrittura.
     *
     * @param digest digest esadecimale della chiave di invio
     * @return riga della prenotazione, o {@code null} se la chiave non è nota
     */
    private ReportSubmissionEntity findByDigest(String digest) {
        List<ReportSubmissionEntity> rows = entityManager
                .createQuery(RESERVATION_BY_DIGEST, ReportSubmissionEntity.class)
                .setParameter("digest", digest)
                .getResultList();
        if (rows.isEmpty()) {
            return null;
        }
        ReportSubmissionEntity row = rows.get(0);
        entityManager.lock(row, LockModeType.PESSIMISTIC_WRITE);
        return row;
    }

    /**
     * Traduce lo stato della riga in un esito della porta.
     *
     * @param row riga della prenotazione
     * @return esito della prenotazione
     */
    private ReservationOutcome outcomeOf(ReportSubmissionEntity row) {
        return SubmissionMapper.toOutcome(row);
    }
}
