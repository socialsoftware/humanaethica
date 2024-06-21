package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime

@DataJpaTest
class DeleteAssessmentMethodTest extends SpockTest {
    Institution institution = Mock()
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Volunteer otherVolunteer = Mock()
    Assessment otherAssessment = Mock()
    Activity otherActivity = Mock()
    Theme theme = Mock()
    def assessment

    def setup() {
        otherActivity.getName() >> ACTIVITY_NAME_2
        otherActivity.getEndingDate() >> DateHandler.now()
        theme.getState() >> Theme.State.APPROVED
        institution.getActivities() >> [otherActivity]
        institution.getAssessments() >> [otherAssessment]

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
        activityDto.applicationDeadline = DateHandler.toISOString(DateHandler.now().minusDays(3))
        activity = new Activity(activityDto, institution, themes)
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and: "an assessment"
        def assessmentDto
        assessmentDto = new AssessmentDto();
        assessmentDto.review = ASSESSMENT_REVIEW_1
        assessment = new Assessment(institution, volunteer, assessmentDto);
    }

    def "delete assessment"() {
        when: "a assessment is deleted"
        assessment.delete()

        then: "checks results"
        volunteer.getAssessments().size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}