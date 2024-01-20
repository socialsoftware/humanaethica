package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "activity")
public class Activity {
    public enum State {REPORTED, APPROVED, SUSPENDED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String region;
    private String description;
    private Integer participantsNumber;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private LocalDateTime applicationDeadline;

    @Enumerated(EnumType.STRING)
    private Activity.State state = Activity.State.APPROVED;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(name = "activity_themes")
    private List<Theme> themes = new ArrayList<>();

    @ManyToOne
    private Institution institution;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Activity() {
    }

    public Activity(ActivityDto activityDto, Institution institution, List<Theme> themes) {
        setInstitution(institution);
        setName(activityDto.getName());
        setRegion(activityDto.getRegion());
        setParticipantsNumber(activityDto.getParticipantsNumber());
        setDescription(activityDto.getDescription());
        setCreationDate(DateHandler.now());
        setStartingDate(DateHandler.toLocalDateTime(activityDto.getStartingDate()));
        setEndingDate(DateHandler.toLocalDateTime(activityDto.getEndingDate()));
        setApplicationDeadline(DateHandler.toLocalDateTime(activityDto.getApplicationDeadline()));

        for (Theme theme : themes) {
            addTheme(theme);
        }

        verifyInvariants();
    }

    public void update(ActivityDto activityDto, List<Theme> themes) {
        setName(activityDto.getName());
        setRegion(activityDto.getRegion());
        setParticipantsNumber(activityDto.getParticipantsNumber());
        setDescription(activityDto.getDescription());
        setStartingDate(DateHandler.toLocalDateTime(activityDto.getStartingDate()));
        setEndingDate(DateHandler.toLocalDateTime(activityDto.getEndingDate()));
        setApplicationDeadline(DateHandler.toLocalDateTime(activityDto.getApplicationDeadline()));

        setThemes(themes);

        verifyInvariants();
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
       if (this.institution.getActivities().stream()
                .anyMatch(activity -> activity != this && activity.getName().equals(name))) {
           throw new HEException(ACTIVITY_ALREADY_EXISTS);
       }

        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getParticipantsNumber() {
        return participantsNumber;
    }

    public void setParticipantsNumber(Integer participantsNumber) {
        this.participantsNumber = participantsNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public Activity.State getState() {
        return state;
    }

    public void setState(Activity.State state) {
        this.state = state;
    }

    public void suspend() {
        if (this.state == State.SUSPENDED) {
            throw new HEException(ACTIVITY_ALREADY_SUSPENDED, this.name);
        }
        this.setState(State.SUSPENDED);
    }

    public void report() {
        if (this.state == State.REPORTED) {
            throw new HEException(ACTIVITY_ALREADY_REPORTED, this.name);
        }
        this.setState(State.REPORTED);
    }

    public void validate() {
        for (Theme theme : themes) {
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED, theme.getCompleteName());
            }
        }
        if (getState() == Activity.State.APPROVED) {
            throw new HEException(ACTIVITY_ALREADY_APPROVED, this.name);
        }
        setState(Activity.State.APPROVED);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void setThemes(List<Theme> newThemes) {
        newThemes.forEach(newTheme -> {
            if (!this.themes.contains(newTheme)) {
                addTheme(newTheme);
            }
        });

        this.themes.forEach(theme -> {
            if (!newThemes.contains(theme)) {
                removeTheme(theme);
            }
        });
    }

    public void addTheme (Theme theme) {
        this.themes.add(theme);
        theme.addActivity(this);
    }

    public void removeTheme(Theme theme) {
        this.themes.remove(theme);
        theme.removeActivity(this);
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addActivity(this);
    }

    public Institution getInstitution() {
        return institution;
    }

    public void verifyInvariants() {
        hasName();
        hasRegion();
        hasDescription();
        hasOneToFiveParticipants();
        hasApplicationDate();
        hasStartingDate();
        hasEndingDate();
        applicationBeforeStart();
        startBeforeEnd();
        themesAreApproved();
    }

    private void hasName() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new HEException(ACTIVITY_NAME_INVALID, this.name);
        }
    }

    private void hasRegion() {
        if (this.region == null || this.region.trim().isEmpty()) {
            throw new HEException(ACTIVITY_REGION_NAME_INVALID, this.region);
        }
    }

    private void hasDescription() {
        if (this.description == null || this.description.trim().isEmpty()) {
            throw new HEException(ACTIVITY_DESCRIPTION_INVALID, this.description);
        }
    }


    private void hasOneToFiveParticipants() {
        if (this.participantsNumber <= 0 || this.participantsNumber > 5) {
            throw new HEException(ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS);
        }
    }

    private void hasApplicationDate() {
        if (this.applicationDeadline == null) {
            throw new HEException(ACTIVITY_INVALID_DATE, "application deadline");
        }
    }

    private void hasStartingDate() {
        if (this.startingDate == null) {
            throw new HEException(ACTIVITY_INVALID_DATE, "starting date");
        }
    }

    private void hasEndingDate() {
        if (this.endingDate == null) {
            throw new HEException(ACTIVITY_INVALID_DATE, "ending date");
        }
    }

    private void applicationBeforeStart() {
        if (!this.applicationDeadline.isBefore(this.startingDate)) {
            throw new HEException(ACTIVITY_APPLICATION_DEADLINE_AFTER_START);
        }
    }

    private void startBeforeEnd() {
        if (!this.startingDate.isBefore(this.endingDate)) {
            throw new HEException(ACTIVITY_START_AFTER_END);
        }
    }

    private void themesAreApproved() {
        for (Theme theme : this.themes) {
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED, theme.getCompleteName());
            }
        }
    }
}
