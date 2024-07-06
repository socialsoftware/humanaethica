package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.util.List;

public class ActivityDto {
    private Integer id;
    private String name;
    private String region;
    private Integer participantsNumberLimit;
    private Integer numberOfEnrollments;
    private Integer numberOfParticipations;
    private String description;
    private String startingDate;
    private String endingDate;
    private String applicationDeadline;
    private String state;
    private String creationDate;
    private String suspensionJustification;
    private String suspensionDate;
    private Integer suspendedByUserId;
    private List<ThemeDto> themes;
    private InstitutionDto institution;

    public ActivityDto(){
    }

    public ActivityDto(Activity activity, boolean deepCopyInstitution){
        setId(activity.getId());
        setName(activity.getName());
        setRegion(activity.getRegion());
        setParticipantsNumberLimit(activity.getParticipantsNumberLimit());
        setNumberOfEnrollments(activity.getEnrollments().size());
        setNumberOfParticipations(activity.getParticipations().size());
        setDescription(activity.getDescription());

        this.themes = activity.getThemes().stream()
                .map(theme->new ThemeDto(theme,false, true, false))
                .toList();

        setState(activity.getState().name());
        setCreationDate(DateHandler.toISOString(activity.getCreationDate()));
        setStartingDate(DateHandler.toISOString(activity.getStartingDate()));
        setEndingDate(DateHandler.toISOString(activity.getEndingDate()));
        setApplicationDeadline(DateHandler.toISOString(activity.getApplicationDeadline()));
        setSuspensionJustification(activity.getSuspensionJustification());
        setSuspensionDate(DateHandler.toISOString(activity.getSuspensionDate()));
        setSuspendedByUserId(activity.getSuspendedByUserId());

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

    public void setSuspensionJustification(String justification) {
        this.suspensionJustification = justification;
    }

    public String getSuspensionJustification() {
        return this.suspensionJustification;
    }

    public void setSuspensionDate(String suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

    public Integer getSuspendedByUserId() {
        return this.suspendedByUserId;
    }

    public void setSuspendedByUserId(Integer userId) {
        this.suspendedByUserId = userId;
    }

    public String getSuspensionDate() {
        return this.suspensionDate;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }


    public Integer getParticipantsNumberLimit() {
        return participantsNumberLimit;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.participantsNumberLimit = participantsNumberLimit;
    }

    public Integer getNumberOfEnrollments() {
        return numberOfEnrollments;
    }

    public void setNumberOfEnrollments(Integer numberOfEnrollments) {
        this.numberOfEnrollments = numberOfEnrollments;
    }

    public Integer getNumberOfParticipations() {
        return numberOfParticipations;
    }

    public void setNumberOfParticipations(Integer numberOfParticipations) {
        this.numberOfParticipations = numberOfParticipations;
    }

    @Override
    public String toString() {
        return "ActivityDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", participantsNumberLimit=" + participantsNumberLimit +
                ", numberOfEnrollments=" + numberOfEnrollments +
                ", numberOfParticipations=" + numberOfParticipations +
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
