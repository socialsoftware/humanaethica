package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Integer participantNumber;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private LocalDateTime applicationDeadline;

    @Enumerated(EnumType.STRING)
    private Activity.State state;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(name = "activity_themes")
    private List<Theme> themes = new ArrayList<>();

    @ManyToOne
    private Institution institution;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Activity() {
    }

    public Activity(String name, String region, int participantNumber, String description, Institution institution, String startingDate, String endingDate, String applicationDeadline, State state) {
        setName(name);
        setRegion(region);
        setParticipantNumber(participantNumber);
        setDescription(description);
        setInstitution(institution);
        setState(state);
        setCreationDate(DateHandler.now());
        setStartingDate(DateHandler.toLocalDateTime(startingDate));
        setEndingDate(DateHandler.toLocalDateTime(endingDate));
        setApplicationDeadline(DateHandler.toLocalDateTime(applicationDeadline));
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getParticipantNumber() {
        return participantNumber;
    }

    public void setParticipantNumber(Integer participantNumber) {
        this.participantNumber = participantNumber;
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
        this.setState(State.SUSPENDED);
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
}
