package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ThemeDto {

    private Integer id;
    private String name;
    private List<Activity> activities = new ArrayList<>();
    private String state;
    private List<InstitutionDto> institutions = new ArrayList<>();

    private ThemeDto parentTheme ;

    private List<ThemeDto> subTheme = new ArrayList<>();

    private Integer level;

    public ThemeDto(){

    }

    public ThemeDto(Theme theme, boolean shallow, boolean shallowTheme) {
        setId(theme.getId());
        setName(theme.getName());
        //setActivities(activities);
        if (!shallow){
            this.institutions = theme.getInstitutions().stream()
                    .map(institution->new InstitutionDto(institution,true, true))
                    .collect(Collectors.toList());
        }
        setState(theme.getState().toString());
        if (!shallowTheme){
            this.subTheme = theme.getSubTheme().stream()
                    .map(themes->new ThemeDto(themes,true,true))
                    .collect(Collectors.toList());
        }
        if (theme.getParentTheme() != null){
            setTheme(new ThemeDto(theme.getParentTheme(),false,true));
        }
        setLevel(theme.getLevel());
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public Integer getLevel() {return level;}

    public void setLevel(Integer level) {this.level = level;}

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

    public List<InstitutionDto> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<InstitutionDto> institutions) {
        this.institutions = institutions;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addTheme (ThemeDto theme){ subTheme.add(theme);}

    public void deleteTheme(ThemeDto theme){subTheme.remove(theme);}

    public ThemeDto getParentTheme(){return this.parentTheme;}

    public void setTheme(ThemeDto theme){this.parentTheme = theme;}

    public List<ThemeDto> getSubThemes(){return this.subTheme;}
}