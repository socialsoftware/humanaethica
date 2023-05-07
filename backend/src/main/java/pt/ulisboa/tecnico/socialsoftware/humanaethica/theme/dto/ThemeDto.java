package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;

import java.util.ArrayList;
import java.util.List;

public class ThemeDto {

    private Integer id;
    private String name;
    //private List<Activity> activities = new ArrayList<>();

    private String state;
    private List<Institution> institutions;

    public ThemeDto(){

    }

    public ThemeDto(Theme theme) {
        setId(theme.getId());
        setName(theme.getName());
        //setActivities(activities);
        setInstitutions(theme.getInstitutions());
        setState(theme.getState().toString());
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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