package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime

@DataJpaTest
class DeleteParticipationMethodTest extends SpockTest {
    Institution institution = Mock()
    Theme theme = Mock()
    Activity otherActivity = Mock()

    def activity
    def volunteer
    def participation
    def otherParticipation
    def participationDto

    def setup() {
        otherActivity.getName() >> ACTIVITY_NAME_2
        theme.getState() >> Theme.State.APPROVED
        institution.getActivities() >> [otherActivity]
        given: "an activity"
        def themes = [theme]
        def activityDto
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumberLimit = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(TWO_DAYS_AGO)
        activityDto.endingDate = DateHandler.toISOString(ONE_DAY_AGO)
        activityDto.applicationDeadline = DateHandler.toISOString(LocalDateTime.now().minusDays(3))
        activity = new Activity(activityDto, institution, themes)
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and: "a participation"
        participationDto = new ParticipationDto()
        participationDto.memberRating = 4
        participationDto.memberReview= MEMBER_REVIEW
        participationDto.volunteerRating= 5
        participationDto.volunteerReview= VOLUNTEER_REVIEW

        participation = new Participation(activity, volunteer, participationDto)
    }

    def "delete participation"() {
        when: "a participation is deleted"
        participation.delete()

        then: "checks results"
        activity.getParticipations().size() == 0
        volunteer.getParticipations().size() == 0

    }

    def "delete one of multiple participations in activity"() {
        given: "another participation for the same activity"
        def otherVolunteer = createVolunteer(USER_2_NAME, USER_2_PASSWORD, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        otherParticipation = new Participation(activity, otherVolunteer, participationDto)

        when: "one participation is deleted"
        participation.delete()

        then: "the other participation remains"
        activity.getParticipations().size() == 1
        activity.getParticipations().contains(otherParticipation)
    }

    def "delete one of multiple participations in volunteer"() {
        given: "another participation for the same volunteer"
        def themes = [theme]
        def activityDto
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumberLimit = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(TWO_DAYS_AGO)
        activityDto.endingDate = DateHandler.toISOString(ONE_DAY_AGO)
        activityDto.applicationDeadline = DateHandler.toISOString(LocalDateTime.now().minusDays(3))
        def otherActivity = new Activity(activityDto, institution, themes)
        participationDto = new ParticipationDto()
        participationDto.memberRating = 4
        participationDto.memberReview= MEMBER_REVIEW
        participationDto.volunteerRating= 5
        participationDto.volunteerReview= VOLUNTEER_REVIEW
        otherParticipation = new Participation(otherActivity, volunteer, participationDto)


        when: "one participation is deleted"
        participation.delete()

        then: "the other participation remains"
        volunteer.getParticipations().size() == 1
        volunteer.getParticipations().contains(otherParticipation)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}