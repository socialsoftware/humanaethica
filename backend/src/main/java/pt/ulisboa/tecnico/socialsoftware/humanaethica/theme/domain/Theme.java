package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.THEME_CAN_NOT_BE_DELETED;


@Entity
@Table(name = "themes")
public class Theme {
    public enum State {SUBMITTED, APPROVED, DELETED}

    @Enumerated(EnumType.STRING)
    private Theme.State state;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @ManyToMany(mappedBy = "themes")
    private List<Activity> activities = new ArrayList<>();
    @ManyToMany
    private List<Institution> institutions = new ArrayList<>();
    @ManyToOne
    private Theme parentTheme ;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Theme> subThemes = new ArrayList<>();
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

    public void addActivity (Activity activity) {
        this.activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        this.activities.remove(activity);
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public void addInstitution (Institution institution){ institutions.add(institution);}

    public void deleteInstitution(Institution institution){institutions.remove(institution);}

    public List<Theme> getSubThemes(){return this.subThemes;}

    public void addTheme (Theme theme){ subThemes.add(theme);}

    public void deleteTheme(Theme theme){
        subThemes.remove(theme);}

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

    public boolean isActive() {
        return state.equals(State.APPROVED);
    }

    public void setState(Theme.State state) {
        this.state = state;
    }

    public Theme.State getState(){return this.state;}

    public void delete() {
        for (Theme subTheme : subThemes) {
            subTheme.delete();
        }

        if (!institutions.isEmpty()) {
            throw new HEException(THEME_CAN_NOT_BE_DELETED, getCompleteName());
        }
        setState(State.DELETED);
    }

    public void approve() {
        if (state != State.APPROVED && getParentTheme() != null) {
            getParentTheme().approve();
        }

        setState(Theme.State.APPROVED);
    }

    public String getCompleteName() {
        return getParentTheme() != null ? getParentTheme().getCompleteName() + "/" + name : name;
    }
}