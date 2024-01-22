package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_DESCRIPTION_INVALID;

public class ActivityDto {
    private Integer id;
    private String name;
    private String region;
    private int participantsNumber;
    private String description;
    private String startingDate;
    private String endingDate;
    private String applicationDeadline;
    private String state;
    private String creationDate;
    private List<ThemeDto> themes;
    private InstitutionDto institution;

    public ActivityDto(){
    }

    public ActivityDto(Activity activity, boolean deepCopyInstitution){
        setId(activity.getId());
        setName(activity.getName());
        setRegion(activity.getRegion());
        setParticipantsNumber(activity.getParticipantsNumber());
        setDescription(activity.getDescription());

        this.themes = activity.getThemes().stream()
                .map(theme->new ThemeDto(theme,false, true, false))
                .toList();

        setState(activity.getState().name());
        setCreationDate(DateHandler.toISOString(activity.getCreationDate()));
        setStartingDate(DateHandler.toISOString(activity.getStartingDate()));
        setEndingDate(DateHandler.toISOString(activity.getEndingDate()));
        setApplicationDeadline(DateHandler.toISOString(activity.getApplicationDeadline()));

        if (deepCopyInstitution && (activity.getInstitution() != null)) {
                setInstitution(new InstitutionDto(activity.getInstitution(), false, false));

        }
    }

    public void setThemes(List<ThemeDto> themes) {
        this.themes = themes;
    }

    public List<ThemeDto> getThemes() {
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }

    public int getParticipantsNumber() {
        return participantsNumber;
    }

    public void setParticipantsNumber(int participantsNumber) {
        this.participantsNumber = participantsNumber;
    }

    @Override
    public String toString() {
        return "ActivityDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", participantsNumber=" + participantsNumber +
                ", description='" + description + '\'' +
                ", startingDate='" + startingDate + '\'' +
                ", endingDate='" + endingDate + '\'' +
                ", applicationDeadline='" + applicationDeadline + '\'' +
                ", state='" + state + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", themes=" + themes +
                ", institution=" + institution +
                '}';
    }
}
