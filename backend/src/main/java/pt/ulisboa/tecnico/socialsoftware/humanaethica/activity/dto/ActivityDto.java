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

    private boolean state;

    private String creationDate;

    private List<Volunteer> volunteers;

    private Theme theme;

    public ActivityDto(){
    }

    public ActivityDto(Activity activity){
        setId(activity.getId());
        setName(activity.getName());
        setRegion(activity.getRegion());
        setTheme(activity.getTheme());
        setState(activity.isActive());
        setCreationDate(DateHandler.toISOString(activity.getCreationDate()));
        setVolunteers(activity.getVolunteers());
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Theme getTheme() {
        return theme;
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

    public boolean isActive() { return state; }

    public void setState(boolean state) { this.state = state; }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setVolunteers(List<Volunteer> volunteers) { this.volunteers = volunteers; }

    public List<Volunteer> getVolunteers() { return volunteers; }
}