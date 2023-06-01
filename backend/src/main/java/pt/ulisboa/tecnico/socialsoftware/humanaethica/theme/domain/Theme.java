package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain;

import org.hibernate.boot.model.source.internal.hbm.AttributesHelper;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import jakarta.persistence.*;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Institution> institutions = new ArrayList<>();

    @ManyToOne
    private Theme parentTheme ;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Theme> subTheme = new ArrayList<>();

    private Integer level = 0;

    public Theme() {
    }

    public Theme(String name, State state, Theme parentTheme) {
        setName(name);
        setState(state);
        setTheme(parentTheme);
        setLevel(parentTheme);
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

    public List<Theme> getSubThemes(){return this.subTheme;}

    public void addTheme (Theme theme){ subTheme.add(theme);}

    public void deleteTheme(Theme theme){subTheme.remove(theme);}

    public Theme getParentTheme(){return this.parentTheme;}

    public void setTheme(Theme theme){
        this.parentTheme = theme;
        if (theme != null){
            theme.addTheme(this);
        }
    }

    public void setLevel(Theme theme){
        if (theme != null){
            this.level = theme.getLevel() + 1;
        }
    }

    public Integer getLevel(){return this.level;}

    public List<Theme> getSubTheme(){return this.subTheme;}

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