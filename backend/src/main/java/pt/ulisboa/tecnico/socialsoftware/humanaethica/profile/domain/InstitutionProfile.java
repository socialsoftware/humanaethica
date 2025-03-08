package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.InstitutionProfileDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "institution_profile")
public class InstitutionProfile {
    public static final int PERCENTAGE_RECENT_ASSESSMENTS = 20;
    public static final int PERCENTAGE_ALL_ASSESSMENTS = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String shortDescription;
    private Integer numMembers;
    private Integer numActivities;
    private Integer numAssessments;
    private Integer numVolunteers;
    private Double averageRating;

    @OneToOne
    private Institution institution;

    @OneToMany(mappedBy = "institutionProfile", fetch = FetchType.EAGER)
    private List<Assessment> selectedAssessments = new ArrayList<>();

    public InstitutionProfile(Institution institution, InstitutionProfileDto institutionProfileDto) {
        setInstitution(institution);
        setShortDescription(institutionProfileDto.getShortDescription());

        institution.getAssessments().forEach(assessment ->
        {
            if (assessmentIdInDto(assessment.getId(), institutionProfileDto.getSelectedAssessments().stream().map(AssessmentDto::getId))) {
                addSelectedAssessment(assessment);
            }
        }
        );

        update();
        verifyInvariants();
    }

    public InstitutionProfile() {
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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        this.institution.setInstitutionProfile(this);
    }

    public List<Assessment> getSelectedAssessments() {
        return selectedAssessments;
    }

    public void setSelectedAssessments(List<Assessment> selectedAssessments) {
        this.selectedAssessments = selectedAssessments;
        for (Assessment assessment : selectedAssessments) {
            assessment.setInstitutionProfile(this);
        }
    }

    public void addSelectedAssessment(Assessment assessment) {
        selectedAssessments.add(assessment);
        assessment.setInstitutionProfile(this);
    }

    public void removeSelectedAssessment(Assessment assessment) {
        selectedAssessments.remove(assessment);
        assessment.setInstitutionProfile(null);
    }

    public void update() {
        setNumMembers(institution.getMembers().size());
        setNumActivities(institution.getActivities().size());
        setNumAssessments(institution.getAssessments().size());

        setNumVolunteers(institution.getActivities().stream()
                .map(Activity::getNumberOfParticipatingVolunteers)
                .reduce(0, Integer::sum));

        setAverageRating(this.calculateAverageRating(institution.getActivities()));
    }

    private Double calculateAverageRating(List<Activity> activities) {
        if (activities.isEmpty()) return 0.0;

        List<Participation> ratedParticipations = activities.stream()
                .map(Activity::getParticipations)
                .flatMap(List::stream)
                .filter(participation -> participation.getVolunteerRating() != null)
                .toList();
        if (ratedParticipations.isEmpty()) return 0.0;

        Integer totalSumRatings = ratedParticipations.stream()
                .map(Participation::getVolunteerRating)
                .reduce(0, Integer::sum);
        return (double) totalSumRatings / ratedParticipations.size();
    }

    private boolean assessmentIdInDto(Integer id, Stream<Integer> idsInDto) {
        return idsInDto.anyMatch(id::equals);
    }

    private void verifyInvariants() {
        shortDescriptionLongEnough();
        minPercentageRecentSelectedAssessments(PERCENTAGE_RECENT_ASSESSMENTS);
        minPercentageSelectedAssessments(PERCENTAGE_ALL_ASSESSMENTS);
    }

    private void shortDescriptionLongEnough() {
        if (this.shortDescription == null || this.shortDescription.trim().length() < 10) {
            throw new HEException(INSTITUTION_PROFILE_SHORT_DESC_TOO_SHORT);
        }
    }

    private void minPercentageRecentSelectedAssessments(int percentage) {
        List<Assessment> sortedAssessments = this.getInstitution().getAssessments().stream().sorted(Comparator.comparing(Assessment::getReviewDate).reversed()).toList();
        long numTotalAssessments = sortedAssessments.size();
        double factor = (double) percentage / 100;
        // if any of the x% most recent assessments is not in selectedAssesssments, invariant is violated
        if (sortedAssessments.subList(0, (int) (numTotalAssessments * factor)).stream()
                .anyMatch(assessment -> !this.selectedAssessments.contains(assessment))) {
            throw new HEException(INSTITUTION_PROFILE_NOT_ENOUGH_RECENT_ASSESSMENTS, percentage);
        }
    }

    private void minPercentageSelectedAssessments(int percentage) {
        long numTotalAssessments = this.getInstitution().getAssessments().size();
        double factor = (double) percentage / 100;
        if (this.selectedAssessments.size() < numTotalAssessments * factor) {
            throw new HEException(INSTITUTION_PROFILE_NOT_ENOUGH_ASSESSMENTS, percentage);
        }
    }

    public void delete() {
        institution.setInstitutionProfile(null);
        selectedAssessments.forEach(assessment -> assessment.setInstitutionProfile(null));
    }
}