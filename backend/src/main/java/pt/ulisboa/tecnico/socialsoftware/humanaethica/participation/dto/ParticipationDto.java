package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class ParticipationDto {
    private Integer id;
    private Integer activityId;
    private Integer volunteerId;
    private Integer memberRating;
    private Integer volunteerRating;
    private String memberReview;
    private String volunteerReview;
    private String acceptanceDate;


    public ParticipationDto() {}


    public ParticipationDto(Participation participation,  User.Role userRole) {
        this.id = participation.getId();
        this.activityId = participation.getActivity().getId();
        this.volunteerId = participation.getVolunteer().getId();
        this.acceptanceDate = DateHandler.toISOString(participation.getAcceptanceDate());

        if (userRole == User.Role.MEMBER) {
            this.memberRating = participation.getMemberRating();
            this.memberReview = participation.getMemberReview();
            if (participation.getMemberReview() != null) {
                this.volunteerRating = participation.getVolunteerRating();
                this.volunteerReview = participation.getVolunteerReview();
            }
        } else if (userRole == User.Role.VOLUNTEER) {
            this.volunteerRating = participation.getVolunteerRating();
            this.volunteerReview = participation.getVolunteerReview();
            if (participation.getVolunteerReview() != null) {
                this.memberRating = participation.getMemberRating();
                this.memberReview = participation.getMemberReview();
            }
        }

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Integer getMemberRating() {
        return memberRating;
    }

    public void setMemberRating(Integer memberRating) {
        this.memberRating = memberRating;
    }

    public Integer getVolunteerRating() {
        return volunteerRating;
    }

    public void setVolunteerRating(Integer volunteerRating) {
        this.volunteerRating = volunteerRating;
    }

    public String getMemberReview() {
        return memberReview;
    }

    public void setMemberReview(String memberReview) {
        this.memberReview = memberReview;
    }

    public String getVolunteerReview() {
        return volunteerReview;
    }

    public void setVolunteerReview(String volunteerReview) {
        this.volunteerReview = volunteerReview;
    }

    public String getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(String acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }
}
