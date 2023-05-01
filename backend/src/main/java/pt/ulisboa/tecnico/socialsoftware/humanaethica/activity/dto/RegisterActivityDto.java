package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto;

public class RegisterActivityDto {

    private String activityName;

    private String activityRegion;

    private String activityTheme;

    public RegisterActivityDto() {
    }

    public RegisterActivityDto(String activityName, String activityRegion, String activityTheme) {
        this.activityName = activityName;
        this.activityRegion = activityRegion;
        this.activityTheme = activityTheme;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }



    public String getActivityRegion() {
        return activityRegion;
    }



    public void setActivityRegion(String activityRegion) {
        this.activityRegion = activityRegion;
    }



    public String getActivityTheme() {
        return activityTheme;
    }



    public void setActivityTheme(String activityTheme) {
        this.activityTheme = activityTheme;
    }

}
