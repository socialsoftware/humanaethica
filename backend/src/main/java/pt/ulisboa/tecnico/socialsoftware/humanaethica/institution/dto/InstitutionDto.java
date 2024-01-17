package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InstitutionDto {
    private Integer id;

    private String email;

    private String name;

    private String nif;

    private boolean active;

    private String creationDate;

    private List<ThemeDto> themeDto = new ArrayList<>();

    private List<ActivityDto> activityDto = new ArrayList<>();

    public InstitutionDto(){
    }

    public InstitutionDto(Institution institution){
        setId(institution.getId());
        setEmail(institution.getEmail());
        setName(institution.getName());
        setNif(institution.getNIF());
        setActive(institution.isActive());
        setCreationDate(DateHandler.toISOString(institution.getCreationDate()));
    }

    public InstitutionDto(Institution institution, boolean deepCopyThemes, boolean deepCopyActivities){
        setId(institution.getId());
        setEmail(institution.getEmail());
        setName(institution.getName());
        setNif(institution.getNIF());
        setActive(institution.isActive());
        setCreationDate(DateHandler.toISOString(institution.getCreationDate()));
        if (deepCopyThemes) {
            this.themeDto = institution.getThemes().stream()
                    .map(theme-> new ThemeDto(theme,false, true, false))
                    .toList();
        }
        if (deepCopyActivities) {
            this.activityDto = institution.getActivities().stream()
                    .map(activity-> new ActivityDto(activity,false))
                    .toList();
        }
    }

    public InstitutionDto(RegisterInstitutionDto registerInstitutionDto){
        setEmail(registerInstitutionDto.getInstitutionEmail());
        setName(registerInstitutionDto.getInstitutionName());
        setNif(registerInstitutionDto.getInstitutionNif());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public List<ThemeDto>getThemes() {
        return themeDto;
    }

    public void setThemes(List<ThemeDto> themeDto) {
        this.themeDto = themeDto;
    }

    public List<ActivityDto>getActivities() {
        return activityDto;
    }

    public void setActivities(List<ActivityDto> activityDto) {
        this.activityDto = activityDto;
    }
}
