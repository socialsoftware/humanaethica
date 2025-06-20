package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_ALREADY_APPROVED;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_ALREADY_REJECTED;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_APPLICATION_DEADLINE_TOO_SOON;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_INVALID;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_NAME_ALREADY_EXISTS;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_REJECTION_JUSTIFICATION_INVALID;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

@Entity
public class ActivitySuggestion {
    private static final int MIN_JUSTIFICATION_SIZE = 10;
    private static final int MAX_JUSTIFICATION_SIZE = 250;
    public static final int DAYS_AFTER_PROPOSAL = 7;

    public enum State {APPROVED, REJECTED, IN_REVIEW}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "number_votes")
    private Integer numberVotes = 0;
    private String name;
    private String description;
    private String region;
    private String rejectionJustification;
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

    @OneToMany(mappedBy = "activitySuggestion")
    private List<Notification> notifications = new ArrayList<>();

    public ActivitySuggestion() {
    }

    public ActivitySuggestion(Institution institution, Volunteer volunteer, ActivitySuggestionDto activitySuggestionDto) {
        setNumberVotes(activitySuggestionDto.getNumberVotes());
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

    public void update(ActivitySuggestionDto activitySuggestionDto, Institution institutio) {
        setNumberVotes(activitySuggestionDto.getNumberVotes());
        setName(activitySuggestionDto.getName());
        setRegion(activitySuggestionDto.getRegion());
        setParticipantsNumberLimit(activitySuggestionDto.getParticipantsNumberLimit());
        setDescription(activitySuggestionDto.getDescription());
        setStartingDate(DateHandler.toLocalDateTime(activitySuggestionDto.getStartingDate()));
        setEndingDate(DateHandler.toLocalDateTime(activitySuggestionDto.getEndingDate()));
        setApplicationDeadline(DateHandler.toLocalDateTime(activitySuggestionDto.getApplicationDeadline()));
        setInstitution(institution);

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumberVotes() {
        return numberVotes;
    }

    public void setNumberVotes(Integer numberVotes) {
        this.numberVotes = (numberVotes != null) ? numberVotes : 0;
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

    public String getRejectionJustification() {
        return this.rejectionJustification;
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

    public void approve() {
        suggestionCannotBeApproved();
        this.setState(State.APPROVED);
    }

    private void suggestionCannotBeApproved() {
        if (this.state == State.APPROVED) {
            throw new HEException(ACTIVITY_SUGGESTION_ALREADY_APPROVED, this.getName());
        }
    }

    public void reject(String justification) {
        suggestionCannotBeRejected();
        this.rejectionJustification = justification;
        this.setState(State.REJECTED);
    }

    private void suggestionCannotBeRejected() {
        if (this.state == State.REJECTED) {
            throw new HEException(ACTIVITY_SUGGESTION_ALREADY_REJECTED, this.name);
        }
    }

    public void upvote() {
        Integer currentVotes = this.getNumberVotes();
        this.setNumberVotes(currentVotes != null ? currentVotes + 1 : 1);
    }

    public void removeUpvote() {
        Integer currentVotes = this.getNumberVotes();
        this.setNumberVotes(currentVotes != null ? currentVotes - 1 : 0);
    }

    public void downvote() {
        Integer currentVotes = this.getNumberVotes();
        this.setNumberVotes(currentVotes != null ? currentVotes - 1 : -1);
    }

    public void removeDownvote() {
        Integer currentVotes = this.getNumberVotes();
        this.setNumberVotes(currentVotes != null ? currentVotes + 1 : 0);
    }

    private void verifyInvariants() {
        descriptionIsLongEnough();
        nameIsUnique();
        applicationDeadlineDaysAfterProposal(DAYS_AFTER_PROPOSAL);
        rejectionJustificationTextSize();
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

    private void rejectionJustificationTextSize() {
        if (this.state != State.REJECTED) {
            return;
        }

        if (this.rejectionJustification == null) {
            throw new HEException(ACTIVITY_SUGGESTION_REJECTION_JUSTIFICATION_INVALID);
        }

        var textSize = this.rejectionJustification.length();
        if (textSize < MIN_JUSTIFICATION_SIZE || textSize > MAX_JUSTIFICATION_SIZE) {
            throw new HEException(ACTIVITY_SUGGESTION_REJECTION_JUSTIFICATION_INVALID);
        }
    }
}
