package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
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

    @Enumerated(EnumType.STRING)
    private Activity.State state;

    @ManyToMany
    private List<Volunteer> volunteers = new ArrayList<>();

    @ManyToMany
    private List<Theme> themes = new ArrayList<>();

    @ManyToOne
    private Institution institution ;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Activity() {
    }
    public Activity(String name, String region, Institution institution, State state) {
        setName(name);
        setRegion(region);
        setThemes(themes);
        setInstitution(institution);
        setState(state);
        setCreationDate(DateHandler.now());

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

    public List<Volunteer> getVolunteers() {
        return volunteers;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void addTheme (Theme theme) {
        this.themes.add(theme);
        theme.addActivity(this);
    }

    public void removeTheme (Integer themeId) {
        for (int i = 0; i < themes.size(); i++) {
            if (themes.get(i).getId().equals(themeId)) {
                themes.remove(i);
                themes.get(i).removeActivity(this.getId());
                return;
            }
        }
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addActivity(this);
    }

    public Institution getInstitution() {
        return institution;
    }

    public void addVolunteer (Volunteer volunteer) {
        this.volunteers.add(volunteer);
        volunteer.addActivities(this);
    }

    public void removeVolunteer (Integer volunteerId) {
        for (int i = 0; i < volunteers.size(); i++) {
            if (volunteers.get(i).getId().equals(volunteerId)) {
                volunteers.remove(i);
                volunteers.get(i).removeActivities(this.getId());
                return;
            }
        }
    }
}
