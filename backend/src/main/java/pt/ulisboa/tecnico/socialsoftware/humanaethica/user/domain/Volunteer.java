package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.VolunteerProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {
    @OneToMany(mappedBy = "volunteer")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer", fetch = FetchType.EAGER)
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer")
    private List<Assessment> assessments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer", fetch = FetchType.EAGER)
    private List<ActivitySuggestion> activitySuggestions = new ArrayList<>();

    @OneToOne(mappedBy = "volunteer")
    private VolunteerProfile volunteerProfile;

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
    }

    public void deleteParticipation(Participation participation) {
        this.participations.remove(participation);
    }

    public List<ActivitySuggestion> getActivitySuggestions() {
        return activitySuggestions;
    }

    public void addActivitySuggestion(ActivitySuggestion activitySuggestion) {
        this.activitySuggestions.add(activitySuggestion);
    }

    public List<Assessment> getAssessments() {
        return this.assessments;
    }

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }

    public void deleteAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }
    
    public void addReport(Report report) {
        this.reports.add(report);
    }

    public void removeReport(Report report) {
        this.reports.remove(report);
    }

    public List<Report> getReports() {
        return reports;
    }

    public VolunteerProfile getVolunteerProfile() {
        return volunteerProfile;
    }

    public void setVolunteerProfile(VolunteerProfile volunteerProfile) {
        this.volunteerProfile = volunteerProfile;
    }
}
