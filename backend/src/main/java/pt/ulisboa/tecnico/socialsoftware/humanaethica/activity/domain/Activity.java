package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "activity")
public class Activity {
    private static final int MIN_JUSTIFICATION_SIZE = 10;
    private static final int MAX_JUSTIFICATION_SIZE = 250;

    public enum State {REPORTED, APPROVED, SUSPENDED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String region;
    private String description;
    private Integer participantsNumberLimit;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private LocalDateTime applicationDeadline;
    private String suspensionJustification;
    private Integer suspendedByUserId;
    private LocalDateTime suspensionDate;
    @Enumerated(EnumType.STRING)
    private Activity.State state = Activity.State.APPROVED;
    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(name = "activity_themes")
    private List<Theme> themes = new ArrayList<>();
    @ManyToOne
    private Institution institution;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @OneToMany(mappedBy = "activity")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "activity")
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "activity")
    private List<Report> reports = new ArrayList<>();

    public Activity() {
    }

    public Activity(ActivityDto activityDto, Institution institution, List<Theme> themes) {
        setInstitution(institution);
        setName(activityDto.getName());
        setRegion(activityDto.getRegion());
        setParticipantsNumberLimit(activityDto.getParticipantsNumberLimit());
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
        setParticipantsNumberLimit(activityDto.getParticipantsNumberLimit());
        setDescription(activityDto.getDescription());
        setStartingDate(DateHandler.toLocalDateTime(activityDto.getStartingDate()));
        setEndingDate(DateHandler.toLocalDateTime(activityDto.getEndingDate()));
        setApplicationDeadline(DateHandler.toLocalDateTime(activityDto.getApplicationDeadline()));

        setThemes(themes);

        verifyInvariants();
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
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

    public Integer getParticipantsNumberLimit() {
        return participantsNumberLimit;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.participantsNumberLimit = participantsNumberLimit;
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

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }

    public void suspend(Integer userId, String justification) {
        activityCannotBeSuspended();

        this.setState(State.SUSPENDED);
        this.suspensionJustification = justification;
        this.suspensionDate = DateHandler.now();
        this.suspendedByUserId = userId;

        suspensionJustificationTextSize();
        suspensionBeforeEnd();
    }

    public String getSuspensionJustification() {
        return this.suspensionJustification;
    }

    public LocalDateTime getSuspensionDate() {
        return this.suspensionDate;
    }

    public Integer getSuspendedByUserId() {return this.suspendedByUserId;}

    public List<Participation> getParticipations() {
        return participations;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
    }

    public void deleteParticipation(Participation participation) {
        this.participations.remove(participation);
    }

    public int getNumberOfParticipatingVolunteers() {
        return this.participations.size();
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }

    private void activityCannotBeSuspended() {
        if (this.state == State.SUSPENDED) {
            throw new HEException(ACTIVITY_ALREADY_SUSPENDED, this.name);
        }
    }

    public void report() {
        activityCannotBeReported();
        this.setState(State.REPORTED);
    }

    private void activityCannotBeReported() {
        if (this.state == State.REPORTED) {
            throw new HEException(ACTIVITY_ALREADY_REPORTED, this.name);
        }
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void addReport(Report report) {
        this.reports.add(report);
    }

    public void removeReport(Report report) {
        this.reports.remove(report);
    }

    public void validate() {
        activityAndThemesMustBeApproved();

        setState(Activity.State.APPROVED);
    }

    private void activityAndThemesMustBeApproved() {
        if (getState() == State.APPROVED) {
            throw new HEException(ACTIVITY_ALREADY_APPROVED, this.name);
        }

        for (Theme theme : themes) {
            if (theme.getState() != Theme.State.APPROVED) {
                throw new HEException(THEME_NOT_APPROVED, theme.getCompleteName());
            }
        }
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
        List<Theme> oldThemes = new ArrayList<>(this.themes);

        newThemes.forEach(newTheme -> {
            if (!this.themes.contains(newTheme)) {
                addTheme(newTheme);
            }
        });

        oldThemes.forEach(theme -> {
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

    private void verifyInvariants() {
        nameIsRequired();
        regionIsRequired();
        descriptionIsRequired();
        hasOneToFiveParticipants();
        applicationDeadlineIsRequired();
        startingDateIsRequired();
        endingDateIsRequired();
        applicationBeforeStart();
        startBeforeEnd();
        themesAreApproved();
        nameIsUnique();
        suspensionJustificationTextSize();
        suspensionBeforeEnd();
    }

    private void nameIsRequired() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new HEException(ACTIVITY_NAME_INVALID, this.name);
        }
    }

    private void regionIsRequired() {
        if (this.region == null || this.region.trim().isEmpty()) {
            throw new HEException(ACTIVITY_REGION_NAME_INVALID, this.region);
        }
    }

    private void descriptionIsRequired() {
        if (this.description == null || this.description.trim().isEmpty()) {
            throw new HEException(ACTIVITY_DESCRIPTION_INVALID, this.description);
        }
    }


    private void hasOneToFiveParticipants() {
        if (this.participantsNumberLimit == null || this.participantsNumberLimit <= 0 || this.participantsNumberLimit > 5) {
            throw new HEException(ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS);
        }
    }

    private void applicationDeadlineIsRequired() {
        if (this.applicationDeadline == null) {
            throw new HEException(ACTIVITY_INVALID_DATE, "Enrollment deadline");
        }
    }

    private void startingDateIsRequired() {
        if (this.startingDate == null) {
            throw new HEException(ACTIVITY_INVALID_DATE, "starting date");
        }
    }

    private void endingDateIsRequired() {
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

    private void nameIsUnique() {
        if (this.institution.getActivities().stream()
                .anyMatch(activity -> activity != this && activity.getName().equals(this.getName()))) {
            throw new HEException(ACTIVITY_ALREADY_EXISTS);
        }
    }

    private void suspensionJustificationTextSize() {
        if (this.state != State.SUSPENDED) {
            return;
        }

        if (this.suspensionJustification == null) {
            throw new HEException(ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID);
        }

        var textSize = this.suspensionJustification.length();
        if (textSize < MIN_JUSTIFICATION_SIZE || textSize > MAX_JUSTIFICATION_SIZE) {
            throw new HEException(ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID);
        }
    }

    private void suspensionBeforeEnd() {
        if (this.suspensionDate != null && this.suspensionDate.isAfter(this.endingDate)) {
            throw new HEException(ACTIVITY_SUSPENSION_AFTER_END);
        }
    }
}
