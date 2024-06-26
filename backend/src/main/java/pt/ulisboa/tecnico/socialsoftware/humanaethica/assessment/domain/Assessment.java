package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String review;
    private LocalDateTime reviewDate;
    @ManyToOne
    private Institution institution;
    @ManyToOne
    private Volunteer volunteer;

    public Assessment() {}

    public Assessment(Institution institution, Volunteer volunteer, AssessmentDto assessmentDto) {
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.now());
        setInstitution(institution);
        setVolunteer(volunteer);

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        this.institution.addAssessment(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        this.volunteer.addAssessment(this);
    }

    public void update(AssessmentDto assessmentDto) {
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.now());

        verifyInvariants();
    }

    public void delete() {
        volunteer.deleteAssessment(this);
        institution.deleteAssessment(this);

        verifyInvariants();
    }

    private void verifyInvariants() {
        reviewIsRequired();
        assessOnlyOnce();
        assessWhenInstitutionHasAtLeastOneFinishedActivity();
    }

    private void reviewIsRequired() {
        if (this.review == null) {
            throw new HEException(ASSESSMENT_REQUIRES_REVIEW);
        } else if (this.review.trim().length() < 10) {
            throw new HEException(ASSESSMENT_REVIEW_TOO_SHORT);
        }
    }

    private void assessOnlyOnce() {
        if (this.institution.getAssessments().stream()
                .anyMatch(assessment -> assessment != this && assessment.getVolunteer() == this.volunteer)) {
            throw new HEException(ASSESSMENT_VOLUNTEER_CAN_ASSESS_INSTITUTION_ONLY_ONCE);
        }
    }

    private void assessWhenInstitutionHasAtLeastOneFinishedActivity() {
        if (this.institution.getActivities().stream()
                .noneMatch(activity -> this.reviewDate.isAfter(activity.getEndingDate()))) {
            throw new HEException(ASSESSMENT_ONLY_IF_INSTITUTION_HAS_FINISHED_ACTIVITIES);
        }
    }
}
