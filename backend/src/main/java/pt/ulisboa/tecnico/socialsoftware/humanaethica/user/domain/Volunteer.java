package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_SUGGESTION_NOT_FOUND;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.VolunteerProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain.Report;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {
    @OneToMany(mappedBy = "volunteer")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer", fetch = FetchType.EAGER)
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer")
    private List<Assessment> assessments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer", fetch = FetchType.EAGER)
    private List<ActivitySuggestion> activitySuggestions = new ArrayList<>();

    @OneToOne(mappedBy = "volunteer")
    private VolunteerProfile volunteerProfile;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "institution_subscriptions",
        joinColumns = @JoinColumn(name = "volunteer_id"),
        inverseJoinColumns = @JoinColumn(name = "institution_id")
    )
    private List<Institution> institutions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "volunteer_votes",
        joinColumns = @JoinColumn(name = "volunteer_id")
    )
    @MapKeyColumn(name = "activity_suggestion_id")
    @Column(name = "votes")
    private Map<Integer, Boolean> activitySuggestionVotes = new HashMap<>();

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
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

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
    }

    public void deleteParticipation(Participation participation) {
        this.participations.remove(participation);
    }

    public List<ActivitySuggestion> getActivitySuggestions() {
        return activitySuggestions;
    }

    public void addActivitySuggestion(ActivitySuggestion activitySuggestion) {
        this.activitySuggestions.add(activitySuggestion);
    }

    public List<Assessment> getAssessments() {
        return this.assessments;
    }

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }

    public void deleteAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }
    
    public void addReport(Report report) {
        this.reports.add(report);
    }

    public void removeReport(Report report) {
        this.reports.remove(report);
    }

    public List<Report> getReports() {
        return reports;
    }

    public VolunteerProfile getVolunteerProfile() {
        return volunteerProfile;
    }

    public void setVolunteerProfile(VolunteerProfile volunteerProfile) {
        this.volunteerProfile = volunteerProfile;
    }

    public void addSubscription(Institution institution) {
        this.institutions.add(institution);
        institution.addSubscriber(this);
    }

    public void removeSubscription(Institution institution) {
        this.institutions.remove(institution);
        institution.deleteSubscriber(this);
    }

    public Map<Integer, Boolean> getActivitySuggestionVotes() {
        return activitySuggestionVotes;
    }

    public void addUpvoteActivitySuggestion(ActivitySuggestion activitySuggestion) {
        if (activitySuggestion == null || activitySuggestion.getId() == null) {
            throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        }
        activitySuggestionVotes.put(activitySuggestion.getId(), true);  // true means upvote
        activitySuggestion.upvote();
    }

    public void removeUpvoteActivitySuggestion(ActivitySuggestion activitySuggestion) {
        if (activitySuggestion == null || activitySuggestion.getId() == null) {
            throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        }
        activitySuggestionVotes.remove(activitySuggestion.getId());
        activitySuggestion.removeUpvote();
    }

    public void addDownvoteActivitySuggestion(ActivitySuggestion activitySuggestion) {
        if (activitySuggestion == null || activitySuggestion.getId() == null) {
            throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        }
        activitySuggestionVotes.put(activitySuggestion.getId(), false);  // false means upvote
        activitySuggestion.downvote();
    }

    public void removeDownvoteActivitySuggestion(ActivitySuggestion activitySuggestion) {
        if (activitySuggestion == null || activitySuggestion.getId() == null) {
            throw new HEException(ACTIVITY_SUGGESTION_NOT_FOUND);
        }
        activitySuggestionVotes.remove(activitySuggestion.getId());
        activitySuggestion.removeDownvote();
    }
}
