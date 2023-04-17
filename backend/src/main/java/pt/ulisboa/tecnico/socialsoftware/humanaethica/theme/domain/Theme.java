package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import org.springframework.security.crypto.keygen.KeyGenerators;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "themes")
public class Theme{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    //private List<Activity> activities = new ArrayList<>();
    @ManyToMany
    private List<Institution> institutions = new ArrayList<>();

    public Theme(String name) {
        setName(name);
    }

    public Theme() {

    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    /*public List<Activity> getActivities() {
        return activities;
    }*/

    /*public void setActivities(ArrayList<Activity> activities){
        this.activities = activities;
    }*/

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }
}