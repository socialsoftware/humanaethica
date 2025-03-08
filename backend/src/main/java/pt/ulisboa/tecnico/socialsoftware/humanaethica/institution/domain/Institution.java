package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain;

import jakarta.persistence.*;
import org.springframework.security.crypto.keygen.KeyGenerators;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.InstitutionProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "institutions")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String email;

    private String nif;

    private boolean active = false;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    private String confirmationToken = "";

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "institution", fetch = FetchType.EAGER, orphanRemoval = true)
    private InstitutionDocument institutionDocument;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "institution", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    private LocalDateTime tokenGenerationDate;

    @ManyToMany(mappedBy = "institutions", fetch = FetchType.LAZY)
    private List<Theme> themes = new ArrayList<>();

    @OneToMany(mappedBy = "institution", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Activity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "institution", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ActivitySuggestion> activitySuggestions = new ArrayList<>();

    @OneToMany(mappedBy = "institution" )
    private List<Assessment> assessments = new ArrayList<>();

    @OneToOne(mappedBy = "institution")
    private InstitutionProfile institutionProfile;

    public Institution() {
    }

    public Institution(String name, String email, String nif) {
        setEmail(email);
        setName(name);
        setNIF(nif);
        generateConfirmationToken();
        setCreationDate(DateHandler.now());
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public List<Member> getMembers() {
        return members;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNIF() {
        return nif;
    }

    public void setNIF(String nIF) {
        nif = nIF;
    }

    public void validate() {
        this.active = true;
    }

    public void delete() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getTokenGenerationDate() {
        return tokenGenerationDate;
    }

    public void setTokenGenerationDate(LocalDateTime tokenGenerationDate) {
        this.tokenGenerationDate = tokenGenerationDate;
    }

    public InstitutionDocument getDocument() {
        return institutionDocument;
    }

    public void setDocument(InstitutionDocument institutionDocument) {
        this.institutionDocument = institutionDocument;
        institutionDocument.setInstitution(this);
    }

    public List<Theme> getThemes() {
        return this.themes;
    }

    public void addTheme(Theme theme) {
        this.themes.add(theme);
        theme.addInstitution(this);
    }

    public void removeTheme(Theme theme) {
        this.themes.remove(theme);
        theme.deleteInstitution(this);
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    public void addActivitySuggestion(ActivitySuggestion activitySuggestion) {
        this.activitySuggestions.add(activitySuggestion);
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }

    public String generateConfirmationToken() {
        String token = KeyGenerators.string().generateKey();
        setTokenGenerationDate(DateHandler.now());
        setConfirmationToken(token);
        return token;
    }

    public void deleteAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }

    public InstitutionProfile getInstitutionProfile() {
        return institutionProfile;
    }

    public void setInstitutionProfile(InstitutionProfile institutionProfile) {
        this.institutionProfile = institutionProfile;
    }
}
