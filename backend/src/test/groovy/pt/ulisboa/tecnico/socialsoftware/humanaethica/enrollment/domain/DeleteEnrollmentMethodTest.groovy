package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class DeleteEnrollmentMethodTest extends SpockTest {
    Institution institution = Mock()
    Theme theme = Mock()
    def enrollmentOne
    def volunteer
    def activity
    def activity2
    def enrollmentTwo

    def setup() {
        theme.getState() >> Theme.State.APPROVED
        institution.getActivities() >> []

        given:"activity"
        def themes = [theme]
        def activityDtoOne
        activityDtoOne = new ActivityDto()
        activityDtoOne.name = ACTIVITY_NAME_1
        activityDtoOne.region = ACTIVITY_REGION_1
        activityDtoOne.participantsNumberLimit = 2
        activityDtoOne.description = ACTIVITY_DESCRIPTION_1
        activityDtoOne.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDtoOne.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDtoOne.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        activity = new Activity(activityDtoOne, institution, themes)

        and: "volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        and: "enrollment"
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_1
        enrollmentOne = new Enrollment(activity, volunteer, enrollmentDto)
    }


    def "delete enrollment"() {

        when: "enrollment is deleted"
        enrollmentOne.delete()

        then: "checks if the enrollment was deleted in the activtiy and volunteer"
        volunteer.getEnrollments().size() == 0
        activity.getEnrollments().size() == 0

    }
   
    def "try to delete enrollment after deadline"() {
        given:
        def activityDtoTwo
        def themes = [theme]
        activityDtoTwo = new ActivityDto()
        activityDtoTwo.name = ACTIVITY_NAME_1
        activityDtoTwo.region = ACTIVITY_REGION_1
        activityDtoTwo.participantsNumberLimit = 2
        activityDtoTwo.description = ACTIVITY_DESCRIPTION_1
        activityDtoTwo.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDtoTwo.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDtoTwo.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        activity2 = new Activity(activityDtoTwo, institution, themes)
        
        and: "enrollment"
        def enrollmentDtoTwo = new EnrollmentDto()
        enrollmentDtoTwo.motivation = ENROLLMENT_MOTIVATION_1
        enrollmentTwo = new Enrollment(activity2, volunteer, enrollmentDtoTwo)
        activity2.setApplicationDeadline(ONE_DAY_AGO)

        when:
        enrollmentTwo.delete()

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_AFTER_DEADLINE
    }
   
    
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}