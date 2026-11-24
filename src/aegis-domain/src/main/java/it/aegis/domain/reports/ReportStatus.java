package it.aegis.domain.reports;

/** Stati e transizioni ammesse da FR-09 del RAD. */
public enum ReportStatus {
    IN_VERIFICATION,
    CONFIRMED,
    REJECTED,
    MARKED_DUPLICATE,
    RESOLVED;

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
