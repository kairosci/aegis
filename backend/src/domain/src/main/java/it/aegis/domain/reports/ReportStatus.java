package it.aegis.domain.reports;

/** Stati e transizioni ammesse da FR-09 del RAD. */
public enum ReportStatus {

    /** Segnalazione appena inviata, in attesa di verifica. */
    IN_VERIFICATION,

    /** Segnalazione verificata e ritenuta valida. */
    CONFIRMED,

    /** Segnalazione verificata e respinta. */
    REJECTED,

    /** Segnalazione riconosciuta come duplicato di un'altra. */
    MARKED_DUPLICATE,

    /** Segnalazione lavorata e chiusa. */
    RESOLVED;

    /**
     * Verifica se lo stato corrente ammette la transizione verso lo stato indicato.
     *
     * @param next stato che si intende raggiungere
     * @return {@code true} se la transizione è ammessa da FR-09 del RAD
     */
    public boolean allows(ReportStatus next) {
        return switch (this) {
            case IN_VERIFICATION -> next == CONFIRMED
                    || next == REJECTED
                    || next == MARKED_DUPLICATE;
            case CONFIRMED -> next == RESOLVED;
            default -> false;
        };
    }
}
