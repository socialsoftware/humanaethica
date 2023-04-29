package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain;

import jakarta.persistence.*;
import org.springframework.security.crypto.keygen.KeyGenerators;
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
    private boolean state;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activity", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Volunteer> volunteers = new ArrayList<>();
    /*
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "institution", fetch = FetchType.EAGER, orphanRemoval = true)
    private Theme theme;
    */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Activity() {
    }
    public Activity(String name, String region) {
        setName(name);
        setRegion(region);
        setState(true);
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

    public void setState(boolean state) {
        this.state = state;
    }

    public void delete() {
        setState(false);
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
