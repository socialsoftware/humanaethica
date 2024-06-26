package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class ReportDto {
    private Integer id;
    private Integer activityId;
    private Integer volunteerId;
    private String volunteerName;
    private String justification;
    private String reportDateTime;


    public ReportDto() {}

    public ReportDto(Report report) {
        this.id = report.getId();
        this.activityId = report.getActivity().getId();
        this.volunteerId = report.getVolunteer().getId();
        this.volunteerName = report.getVolunteer().getName();
        this.justification = report.getJustification();
        this.reportDateTime = DateHandler.toISOString(report.getReportDateTime());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(String reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

}
