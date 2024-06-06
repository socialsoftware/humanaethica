package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDateTime;
    @ManyToOne
    private Activity activity;
    @ManyToOne
    private Volunteer volunteer;

    public Enrollment() {}

    public Enrollment(Activity activity, Volunteer volunteer, EnrollmentDto enrollmentDto) {
        setActivity(activity);
        setVolunteer(volunteer);
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDateTime(LocalDateTime.now());

        verifyInvariants();
    }

    public void update(EnrollmentDto enrollmentDto) {  
        setMotivation(enrollmentDto.getMotivation());

        editOrDeleteEnrollmentBeforeDeadline();
        verifyInvariants();
    }

    public void delete(){
        volunteer.removeEnrollment(this);
        activity.removeEnrollment(this);

        editOrDeleteEnrollmentBeforeDeadline();
        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public LocalDateTime getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(LocalDateTime enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        this.activity.addEnrollment(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        this.volunteer.addEnrollment(this);
    }

    private void verifyInvariants() {
        motivationIsRequired();
        enrollOnce();
        enrollBeforeDeadline();
    }

    private void motivationIsRequired() {
        if (this.motivation == null || this.motivation.trim().length() < 10) {
            throw new HEException(ENROLLMENT_REQUIRES_MOTIVATION);
        }
    }

    private void enrollOnce() {
        if (this.activity.getEnrollments().stream()
                .anyMatch(enrollment -> enrollment != this && enrollment.getVolunteer() == this.volunteer)) {
            throw new HEException(ENROLLMENT_VOLUNTEER_IS_ALREADY_ENROLLED);
        }
    }

    private void enrollBeforeDeadline() {
        if (this.enrollmentDateTime.isAfter(this.activity.getApplicationDeadline())) {
            throw new HEException(ENROLLMENT_AFTER_DEADLINE);
        }
    }

    private void editOrDeleteEnrollmentBeforeDeadline() {
        if (LocalDateTime.now().isAfter(this.activity.getApplicationDeadline())) {
            throw new HEException(ENROLLMENT_AFTER_DEADLINE);
        }
    }
}
