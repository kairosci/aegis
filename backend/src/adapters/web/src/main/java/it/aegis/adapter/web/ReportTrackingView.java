package it.aegis.adapter.web;

import it.aegis.application.usecase.PublicReportStatus;
import it.aegis.application.usecase.TrackReport;
import it.aegis.config.MessageSource;
import it.aegis.domain.reports.TrackingCode;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Locale;

/**
 * Bean della vista di tracking. Delega al caso d'uso e non contiene regole di dominio né testi localizzati.
 *
 * <p>Mantiene il codice inserito e l'ultimo stato ottenuto per la durata della
 * richiesta. I messaggi di errore sono risolti tramite la sorgente messaggi
 * configurata, usando la lingua della vista corrente. La classe non è finale
 * perché lo scope di richiesta richiede un proxy CDI.</p>
 */
@Named
@RequestScoped
public class ReportTrackingView {

    /** Caso d'uso che restituisce lo stato pubblico della segnalazione. */
    private final TrackReport trackReport;

    /** Sorgente dei testi localizzati della vista. */
    private final MessageSource messages;

    /** Codice di tracking inserito dall'utente. */
    private String code;

    /** Ultimo stato pubblico ottenuto, o null dopo un tentativo non valido. */
    private PublicReportStatus status;

    /**
     * Crea il bean con il caso d'uso e la sorgente dei messaggi.
     *
     * @param trackReport caso d'uso che restituisce lo stato pubblico della segnalazione
     * @param messages sorgente dei testi localizzati della vista
     */
    @Inject
    public ReportTrackingView(TrackReport trackReport, MessageSource messages) {
        this.trackReport = trackReport;
        this.messages = messages;
    }

    /**
     * Cerca lo stato della segnalazione usando il codice inserito nella vista.
     *
     * <p>Il codice viene privato degli spazi iniziali e finali. Un codice non
     * valido azzera lo stato visibile e aggiunge un messaggio di errore alla
     * vista senza interrompere la richiesta.</p>
     */
    public void track() {
        try {
            status = trackReport.track(new TrackingCode(code == null ? "" : code.trim()));
        } catch (IllegalArgumentException cause) {
            status = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message("tracking.unavailable"), null));
        }
    }

    /**
     * Restituisce il codice di tracking inserito nella vista.
     *
     * @return codice inserito dall'utente, o null se non è stato ancora impostato
     */
    public String getCode() {
        return code;
    }

    /**
     * Imposta il codice di tracking inserito nella vista.
     *
     * @param code codice inserito dall'utente
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Restituisce l'ultimo stato pubblico ottenuto.
     *
     * @return stato della segnalazione, o null se non è ancora disponibile
     */
    public PublicReportStatus getStatus() {
        return status;
    }

    /**
     * Restituisce l'etichetta italiana dello stato pubblico.
     *
     * @return etichetta localizzata o stringa vuota se non è disponibile uno stato
     */
    public String getStatusLabel() {
        if (status == null) {
            return "";
        }
        String key = switch (status.status()) {
            case IN_VERIFICATION -> "report.status.in-verification";
            case CONFIRMED -> "report.status.confirmed";
            case REJECTED -> "report.status.rejected";
            case MARKED_DUPLICATE -> "report.status.duplicate";
            case RESOLVED -> "report.status.resolved";
        };
        return message(key);
    }

    /**
     * Restituisce un testo della vista dal bundle esterno.
     *
     * @param key chiave logica del messaggio
     * @return testo localizzato associato alla chiave
     */
    public String text(String key) {
        return message(key);
    }

    /**
     * Risolve una chiave di messaggio nella lingua della vista corrente.
     *
     * @param key chiave logica del messaggio
     * @return testo localizzato letto dalla sorgente configurata
     * @throws IllegalStateException se il bundle o la chiave non sono disponibili
     */
    private String message(String key) {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return messages.get(key, locale);
    }
}
