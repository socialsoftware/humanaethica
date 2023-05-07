package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain;

import jakarta.persistence.*;
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


    //private List<Activity> activities = new ArrayList<>();
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