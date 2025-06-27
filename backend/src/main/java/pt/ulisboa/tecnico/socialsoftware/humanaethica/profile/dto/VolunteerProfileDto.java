package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.VolunteerProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

import java.util.List;

public class VolunteerProfileDto {
    private Integer id;
    
    private String shortBio;
    private Integer numTotalEnrollments;
    private Integer numTotalParticipations;
    private Integer numTotalAssessments;
    private Double averageRating;
    private UserDto volunteer;
    private List<ParticipationDto> selectedParticipations;

    public VolunteerProfileDto() {}

    public VolunteerProfileDto(VolunteerProfile volunteerProfile){
        setId(volunteerProfile.getId());
        setShortBio(volunteerProfile.getShortBio());
        setNumTotalEnrollments(volunteerProfile.getNumTotalEnrollments());
        setNumTotalParticipations(volunteerProfile.getNumTotalParticipations());
        setNumTotalAssessments(volunteerProfile.getNumTotalAssessments());
        setAverageRating(volunteerProfile.getAverageRating());
        setVolunteer(new UserDto(volunteerProfile.getVolunteer()));
        setSelectedParticipations(volunteerProfile.getSelectedParticipations().stream()
                .map(participation -> new ParticipationDto(participation, User.Role.VOLUNTEER))
                .toList()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public Integer getNumTotalEnrollments() {
        return numTotalEnrollments;
    }

    public void setNumTotalEnrollments(Integer numTotalEnrollments) {
        this.numTotalEnrollments = numTotalEnrollments;
    }

    public Integer getNumTotalParticipations() {
        return numTotalParticipations;
    }

    public void setNumTotalParticipations(Integer numTotalParticipations) {
        this.numTotalParticipations = numTotalParticipations;
    }

    public Integer getNumTotalAssessments() {
        return numTotalAssessments;
    }

    public void setNumTotalAssessments(Integer numTotalAssessments) {
        this.numTotalAssessments = numTotalAssessments;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public UserDto getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
    }

    public List<ParticipationDto> getSelectedParticipations() {
        return selectedParticipations;
    }

    public void setSelectedParticipations(List<ParticipationDto> selectedParticipations) {
        this.selectedParticipations = selectedParticipations;
    }

    @Override
    public String toString() {
        return "VolunteerProfileDto{" +
                "id=" + id +
                ", shortBio='" + shortBio + '\'' +
                ", numTotalEnrollments=" + numTotalEnrollments +
                ", numTotalParticipations=" + numTotalParticipations +
                ", numTotalAssessments=" + numTotalAssessments +
                ", averageRating=" + averageRating +
                ", volunteer=" + volunteer +
                ", selectedParticipations=" + selectedParticipations +
                '}';
    }
}