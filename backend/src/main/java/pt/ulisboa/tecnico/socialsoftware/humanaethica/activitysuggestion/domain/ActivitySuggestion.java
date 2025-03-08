package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
public class ActivitySuggestion {

    public static final int DAYS_AFTER_PROPOSAL = 7;

    public enum State {APPROVED, REJECTED, IN_REVIEW}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String region;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    private LocalDateTime applicationDeadline;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private Integer participantsNumberLimit;
    @Enumerated(EnumType.STRING)
    private ActivitySuggestion.State state = State.IN_REVIEW;

    @ManyToOne
    private Institution institution;

    @ManyToOne
    private Volunteer volunteer;

    public ActivitySuggestion() {
    }

    public ActivitySuggestion(Institution institution, Volunteer volunteer, ActivitySuggestionDto activitySuggestionDto) {
        setName(activitySuggestionDto.getName());
        setDescription(activitySuggestionDto.getDescription());
        setRegion(activitySuggestionDto.getRegion());
        setCreationDate(DateHandler.now());
        setApplicationDeadline(DateHandler.toLocalDateTime(activitySuggestionDto.getApplicationDeadline()));
        setStartingDate(DateHandler.toLocalDateTime(activitySuggestionDto.getStartingDate()));
        setEndingDate(DateHandler.toLocalDateTime(activitySuggestionDto.getEndingDate()));
        setParticipantsNumberLimit(activitySuggestionDto.getParticipantsNumberLimit());
        setState(State.IN_REVIEW);

        setInstitution(institution);
        setVolunteer(volunteer);

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public LocalDateTime getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDateTime startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDateTime getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDateTime endingDate) {
        this.endingDate = endingDate;
    }

    public Integer getParticipantsNumberLimit() {
        return participantsNumberLimit;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.participantsNumberLimit = participantsNumberLimit;
    }

    public ActivitySuggestion.State getState() {
        return state;
    }

    public void setState(ActivitySuggestion.State state) {
        this.state = state;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addActivitySuggestion(this);
    }

    public Institution getInstitution() {
        return institution;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        this.volunteer.addActivitySuggestion(this);
    }

    private void verifyInvariants() {
        descriptionIsLongEnough();
        nameIsUnique();
        applicationDeadlineDaysAfterProposal(DAYS_AFTER_PROPOSAL);
    }

    private void descriptionIsLongEnough() {
        if (this.description == null || this.description.trim().length() < 10) {
            throw new HEException(ACTIVITY_SUGGESTION_DESCRIPTION_INVALID, this.description);
        }
    }

    private void nameIsUnique() {
        if (this.volunteer.getActivitySuggestions().stream()
                .anyMatch(activitySuggestion -> activitySuggestion != this && activitySuggestion.getName().equals(this.getName()))) {
            throw new HEException(ACTIVITY_SUGGESTION_NAME_ALREADY_EXISTS, this.name);
        }
    }

    private void applicationDeadlineDaysAfterProposal(int daysAfterProposal) {
        if(this.applicationDeadline == null || this.applicationDeadline.isBefore(this.creationDate.plusDays(daysAfterProposal))) {
            throw new HEException(ACTIVITY_SUGGESTION_APPLICATION_DEADLINE_TOO_SOON, daysAfterProposal);
        }
    }
}