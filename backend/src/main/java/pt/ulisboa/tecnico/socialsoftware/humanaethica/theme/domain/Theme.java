package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import org.springframework.security.crypto.keygen.KeyGenerators;
import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "theme")
public class Theme{

    public enum State {SUBMITTED, APPROVED, DELETED}

    @Enumerated(EnumType.STRING)
    private Theme.State state;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;


    @ManyToMany
    private List<Activity> activities = new ArrayList<>();

    @ManyToMany
    private List<Institution> institutions = new ArrayList<>();

    public Theme() {
    }

    public Theme(String name, State state) {
        setName(name);
        setState(state);
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities){
        this.activities = activities;
    }

    public void addActivity (Activity activity) {
        this.activities.add(activity);
    }

    public void removeActivity (Integer activityId) {
        for (Activity activity: activities) {
            if (activity.getId().equals(activityId)) {
                activities.remove(activity);
                return;
            }
        }
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public void addInstitution (Institution institution){ institutions.add(institution);}

    public void deleteInstitution(Institution institution){institutions.remove(institution);}

    public boolean isActive() {
        return state.equals(State.APPROVED);
    }

    public void setState(Theme.State state) {
        this.state = state;
    }

    public Theme.State getState(){return this.state;}

    public void delete() {
        if (institutions.size() == 0){
            setState(State.DELETED);
        }
    }
}