package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class ActivitySuggestionDto {
    private Integer id;
    private Integer numberVotes;
    private String name;
    private String description;
    private String region;
    private String creationDate;
    private String applicationDeadline;
    private String startingDate;
    private String endingDate;
    private Integer participantsNumberLimit;
    private String state;

    private InstitutionDto institution;
    private Integer volunteerId;

    public ActivitySuggestionDto() {
    }

    public ActivitySuggestionDto(ActivitySuggestion activitySuggestion, boolean deepCopyInstitution) {
        setId(activitySuggestion.getId());
        setNumberVotes(activitySuggestion.getNumberVotes());
        setName(activitySuggestion.getName());
        setDescription(activitySuggestion.getDescription());
        setRegion(activitySuggestion.getRegion());
        setCreationDate(DateHandler.toISOString(activitySuggestion.getCreationDate()));
        setApplicationDeadline(DateHandler.toISOString(activitySuggestion.getApplicationDeadline()));
        setStartingDate(DateHandler.toISOString(activitySuggestion.getStartingDate()));
        setEndingDate(DateHandler.toISOString(activitySuggestion.getEndingDate()));
        setParticipantsNumberLimit(activitySuggestion.getParticipantsNumberLimit());
        setState(activitySuggestion.getState().name());
        setVolunteerId(activitySuggestion.getVolunteer().getId());

        if (deepCopyInstitution && (activitySuggestion.getInstitution() != null)) {
                setInstitution(new InstitutionDto(activitySuggestion.getInstitution(), false, false));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberVotes() {
        return numberVotes;
    }

    public void setNumberVotes(Integer numberVotes) {
        this.numberVotes = (numberVotes != null) ? numberVotes : 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public Integer getParticipantsNumberLimit() {
        return participantsNumberLimit;
    }

    public void setParticipantsNumberLimit(Integer participantsNumberLimit) {
        this.participantsNumberLimit = participantsNumberLimit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    @Override
    public String toString() {
        return "ActivitySuggestionDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", region='" + region + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", applicationDeadline='" + applicationDeadline + '\'' +
                ", startingDate='" + startingDate + '\'' +
                ", endingDate='" + endingDate + '\'' +
                ", participantsNumberLimit=" + participantsNumberLimit +
                ", state='" + state + '\'' +
                '}';
    }
}