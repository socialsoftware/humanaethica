package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain;

import jakarta.persistence.*;
import org.springframework.security.crypto.keygen.KeyGenerators;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String region;
    private boolean state = false;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activity", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Volunteer> volunteers = new ArrayList<>();

    @ManyToOne
    private Theme theme;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Activity() {
    }
    public Activity(String name, String region, Theme theme) {
        setName(name);
        setRegion(region);
        setTheme(theme);
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

    public boolean isActive() {
        return state;
    }

    public void validate(boolean state) {
        this.state = state;
    }

    public void delete() {
        this.state = false;
    }

    public void validate() {
        this.state = true;
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

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Theme getTheme() {
        return theme;
    }

    public void addVolunteer (Volunteer volunteer) {
        this.volunteers.add(volunteer);
    }

    public void removeVolunteer (int volunteerId) {
        for (int i = 0; i < volunteers.size(); i++) {
            if (volunteers.get(i).getId() == volunteerId) {
                volunteers.remove(i);
                return;
            }
        }
    }
}
