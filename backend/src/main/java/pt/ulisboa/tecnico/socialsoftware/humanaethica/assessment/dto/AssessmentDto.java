package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

public class AssessmentDto {
    private Integer id;
    private Integer institutionId;
    private String institutionName;
    private Integer volunteerId;
    private String volunteerName;
    private String review;
    private String reviewDate;


    public AssessmentDto() {
    }

    public AssessmentDto(Assessment assessment) {
        this.id = assessment.getId();

        Institution institution = assessment.getInstitution();
        this.institutionId = institution.getId();
        this.institutionName = institution.getName();

        this.volunteerId = assessment.getVolunteer().getId();
        this.volunteerName = assessment.getVolunteer().getName();
        this.review = assessment.getReview();
        this.reviewDate = DateHandler.toISOString(assessment.getReviewDate());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName(){ return institutionName; }

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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }
}
