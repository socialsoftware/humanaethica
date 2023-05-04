package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityDto {
    private Integer id;

    private String name;

    private String region;

    private String state;

    private String creationDate;

    private List<Volunteer> volunteers;

    private List<Theme> themes;

    public ActivityDto(){
    }

    public ActivityDto(Activity activity){
        setId(activity.getId());
        setName(activity.getName());
        setRegion(activity.getRegion());
        setThemes(activity.getThemes());
        setState(activity.getState().toString());
        setCreationDate(DateHandler.toISOString(activity.getCreationDate()));
        setVolunteers(activity.getVolunteers());
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setVolunteers(List<Volunteer> volunteers) { this.volunteers = volunteers; }

    public List<Volunteer> getVolunteers() { return volunteers; }
}
