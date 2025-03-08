package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.VolunteerProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "volunteer_profile")
public class VolunteerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String shortBio;
    private Integer numTotalEnrollments;
    private Integer numTotalParticipations;
    private Integer numTotalAssessments;
    private Double averageRating;

    @OneToOne
    private Volunteer volunteer;

    @OneToMany(mappedBy = "volunteerProfile", fetch = FetchType.EAGER)
    private List<Participation> selectedParticipations = new ArrayList<>();

    public VolunteerProfile(Volunteer volunteer, VolunteerProfileDto volunteerProfileDto) {
        setVolunteer(volunteer);
        setShortBio(volunteerProfileDto.getShortBio());

        volunteer.getParticipations().forEach(participation -> {
            if (participationIdInDto(participation.getId(), volunteerProfileDto.getSelectedParticipations().stream().map(ParticipationDto::getId))) {
                addSelectedParticipation(participation);
            }
        }
        );

        update();
        verifyInvariants();
    }

    public VolunteerProfile() { }

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

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        this.volunteer.setVolunteerProfile(this);
    }

    public List<Participation> getSelectedParticipations() {
        return selectedParticipations;
    }

    public void setSelectedParticipations(List<Participation> participations) {
        this.selectedParticipations = participations;
        for (Participation participation : participations) {
            participation.setVolunteerProfile(this);
        }
    }

    public void addSelectedParticipation(Participation participation) {
        this.selectedParticipations.add(participation);
        participation.setVolunteerProfile(this);
    }

    public void removeSelectedParticipation(Participation participation) {
        this.selectedParticipations.remove(participation);
        participation.setVolunteerProfile(null);
    }

    public void update() {
        setNumTotalEnrollments(volunteer.getEnrollments().size());
        setNumTotalParticipations(volunteer.getParticipations().size());
        setNumTotalAssessments(volunteer.getAssessments().size());
        setAverageRating(this.calculateAverageRating(volunteer.getParticipations()));
    }

    private Double calculateAverageRating(List<Participation> participations) {
        if (participations.isEmpty()) return 0.0;

        List<Participation> ratedParticipations = participations.stream()
                .filter(participation -> participation.getMemberRating() != null)
                .toList();
        Integer totalSumRatings = ratedParticipations.stream()
                .map(Participation::getMemberRating)
                .reduce(0, Integer::sum);
        return (double) totalSumRatings / ratedParticipations.size();
    }

    private boolean participationIdInDto(Integer id, Stream<Integer> idsInDto) {
        return idsInDto.anyMatch(id::equals);
    }

    private void verifyInvariants() {
        shortBioLongEnough();
        selectedParticipationsMustBeAssessed();
        minSelectedParticipationsValid();
    }

    private void shortBioLongEnough() {
        if (this.shortBio == null || this.shortBio.trim().length() < 10) {
            throw new HEException(VOLUNTEER_PROFILE_SHORT_BIO_TOO_SHORT);
        }
    }

    private void selectedParticipationsMustBeAssessed() {
        if (this.selectedParticipations.stream()
                .anyMatch(participation -> participation.getMemberReview() == null)) {
            throw new HEException(VOLUNTEER_PROFILE_SELECTED_PARTICIPATIONS_ASSESSED);
        }
    }

    private void minSelectedParticipationsValid() {
        long totalNumAssessedParticipations = this.getVolunteer().getParticipations().stream()
                .filter(participation -> participation.getMemberReview() != null).count();
        long totalNumParticipations = this.getVolunteer().getParticipations().size();
        if (this.selectedParticipations.size() < Math.min(totalNumParticipations / 2, totalNumAssessedParticipations)) {
            throw new HEException(VOLUNTEER_PROFILE_SELECTED_PARTICIPATIONS_MINIMUM);
        }
    }

    public void delete() {
        volunteer.setVolunteerProfile(null);
        selectedParticipations.forEach(participation -> participation.setVolunteerProfile(null));
    }
}