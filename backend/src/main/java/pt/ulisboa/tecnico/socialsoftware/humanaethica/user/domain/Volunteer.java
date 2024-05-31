package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {
    @OneToMany(mappedBy = "volunteer")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer")
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer")
    private List<Assessment> assessments = new ArrayList<>();

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

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }
}
