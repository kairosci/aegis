package it.aegis.adapter.web;

import it.aegis.application.usecase.PublicReportStatus;
import it.aegis.application.usecase.TrackReport;
import it.aegis.domain.reports.TrackingCode;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Locale;
import java.util.ResourceBundle;

/** Bean della vista di tracking. Delega al caso d'uso e non contiene regole di dominio. */
@Named
@RequestScoped
public final class ReportTrackingView {

    private static final String MESSAGE_BUNDLE = "it.aegis.adapter.web.messages";

    private final TrackReport trackReport;
    private String code;
    private PublicReportStatus status;

    @Inject
    public ReportTrackingView(TrackReport trackReport) {
        this.trackReport = trackReport;
    }

    public void track() {
        try {
            status = trackReport.track(new TrackingCode(code == null ? "" : code.trim()));
        } catch (IllegalArgumentException cause) {
            status = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message("tracking.invalid"), null));
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PublicReportStatus getStatus() {
        return status;
    }

    private String message(String key) {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return ResourceBundle.getBundle(MESSAGE_BUNDLE, locale).getString(key);
    }
}
