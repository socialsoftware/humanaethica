package pt.ulisboa.tecnico.socialsoftware.humanaethica.application.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.application.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Long id;
    private String motivation;

    private String enrollmentDateTime;


    public EnrollmentDto() {}

    public EnrollmentDto(Enrollment enrollment) {
        this.id = enrollment.getId();
        this.motivation = enrollment.getMotivation();
        this.enrollmentDateTime = DateHandler.toISOString(enrollment.getEnrollmentDateTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(String enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }
}
