package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
public class Report {
    private static final int JUSTIFICAITON_MAX_SIZE = 256;
    private static final int JUSTIFICAITON_MIN_SIZE = 10;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String justification;
    private LocalDateTime reportDateTime;
    @ManyToOne
    private Activity activity;
    @ManyToOne
    private Volunteer volunteer;

    public Report() {}

    public Report(Activity activity, Volunteer volunteer, ReportDto reportDto){
        setActivity(activity);
        setVolunteer(volunteer);
        setJustification(reportDto.getJustification());
        setReportDateTime(LocalDateTime.now());

        verifyInvariants();
    }

    public void delete(){
        volunteer.removeReport(this);
        activity.removeReport(this);

        deleteReportBeforeActivityEnd();
        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(LocalDateTime reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        this.activity.addReport(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        this.volunteer.addReport(this);
    }

    private void verifyInvariants() {
        justificationIsRequired();
        reportOnce();
        reportBeforeActivityEnd();
    }

    private void justificationIsRequired() {
        if (this.justification == null || this.justification.trim().length() > JUSTIFICAITON_MAX_SIZE 
            || this.justification.trim().length() < JUSTIFICAITON_MIN_SIZE) {
                throw new HEException(REPORT_REQUIRES_JUSTIFICATION);
        }
    }

    private void reportOnce() {
        if (this.activity.getReports().stream()
                .anyMatch(report -> report != this && report.getVolunteer() == this.volunteer)) {
            throw new HEException(REPORT_ACTIVTIY_IS_ALREADY_REPORTED);
        }
    }

    private void reportBeforeActivityEnd() {
        if (this.reportDateTime.isAfter(this.activity.getEndingDate())) {
            throw new HEException(REPORT_AFTER_ACTIVTY_CLOSED);
        }
    }

    private void deleteReportBeforeActivityEnd() {
        if (LocalDateTime.now().isAfter(this.activity.getEndingDate())) {
            throw new HEException(REPORT_AFTER_ACTIVTY_CLOSED);
        }
    }
}
