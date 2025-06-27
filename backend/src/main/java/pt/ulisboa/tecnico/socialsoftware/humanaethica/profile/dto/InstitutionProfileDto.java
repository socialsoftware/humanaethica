package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.InstitutionProfile;

import java.util.List;

public class InstitutionProfileDto {
    private Integer id;

    private String shortDescription;
    private Integer numMembers;
    private Integer numActivities;
    private Integer numAssessments;
    private Integer numVolunteers;
    private Double averageRating;
    private InstitutionDto institution;
    private List<AssessmentDto> selectedAssessments;

    public InstitutionProfileDto() {}

    public InstitutionProfileDto(InstitutionProfile institutionProfile){
        setId(institutionProfile.getId());
        setShortDescription(institutionProfile.getShortDescription());
        setNumMembers(institutionProfile.getNumMembers());
        setNumActivities(institutionProfile.getNumActivities());
        setNumAssessments(institutionProfile.getNumAssessments());
        setNumVolunteers(institutionProfile.getNumVolunteers());
        setAverageRating(institutionProfile.getAverageRating());
        setInstitution(new InstitutionDto(institutionProfile.getInstitution()));
        setSelectedAssessments(institutionProfile.getSelectedAssessments().stream().map(AssessmentDto::new).toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Integer getNumMembers() {
        return numMembers;
    }

    public void setNumMembers(Integer numMembers) {
        this.numMembers = numMembers;
    }

    public Integer getNumActivities() {
        return numActivities;
    }

    public void setNumActivities(Integer numActivities) {
        this.numActivities = numActivities;
    }

    public Integer getNumAssessments() {
        return numAssessments;
    }

    public void setNumAssessments(Integer numAssessments) {
        this.numAssessments = numAssessments;
    }

    public Integer getNumVolunteers() {
        return numVolunteers;
    }

    public void setNumVolunteers(Integer numVolunteers) {
        this.numVolunteers = numVolunteers;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }

    public List<AssessmentDto> getSelectedAssessments() {
        return selectedAssessments;
    }

    public void setSelectedAssessments(List<AssessmentDto> selectedAssessments) {
        this.selectedAssessments = selectedAssessments;
    }

    @Override
    public String toString() {
        return "InstitutionProfileDto{" +
                "id=" + id +
                ", shortDescription='" + shortDescription + '\'' +
                ", numMembers=" + numMembers +
                ", numActivities=" + numActivities +
                ", numAssessments=" + numAssessments +
                ", numVolunteers=" + numVolunteers +
                ", averageRating=" + averageRating +
                ", institution=" + institution +
                ", selectedAssessments=" + selectedAssessments +
                '}';
    }
}