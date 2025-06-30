package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class UpdateEnrollmentMethodTest extends SpockTest {
    Institution institution = Mock()
    Activity activity = Mock()
    Enrollment otherEnrollment = Mock()
    Theme theme = Mock()

    def enrollment
    def enrollmentDtoOne
    def enrollmentDtoEdit

    def activity2
    def enrollmentTwo
    def volunteer


    def setup() {
        given:
        enrollmentDtoOne = new EnrollmentDto()
        enrollmentDtoOne.motivation = SpockTest.ENROLLMENT_MOTIVATION_1
        activity.getEnrollments() >> [otherEnrollment]
        activity.getApplicationDeadline() >> SpockTest.IN_TWO_DAYS

        and: "volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, State.APPROVED)

        and: "enrollment"
        enrollment = new Enrollment(activity, volunteer, EnrollmentDtoOne)
        enrollmentDtoEdit = new EnrollmentDto()
    }


    def "update enrollment"() {
        given:
        enrollmentDtoEdit.motivation = SpockTest.ENROLLMENT_MOTIVATION_2

        when:
        enrollment.update(enrollmentDtoEdit)
        sleep(1)

        then: "checks results"
        enrollment.getMotivation() == SpockTest.ENROLLMENT_MOTIVATION_2
        enrollment.enrollmentDateTime.isBefore(LocalDateTime.now())
        enrollment.activity == activity
        enrollment.volunteer == volunteer
        
    }

    @Unroll
    def "edit enrollment and violate motivation is required invariant: motivation=#motivation"() {
      given:
      enrollmentDtoEdit.motivation = motivation

      when:
      enrollment.update(enrollmentDtoEdit)

      then:
      def error = thrown(HEException)
      error.getErrorMessage() == errorMessage

      where:
        motivation || errorMessage
        null       || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        "   "      || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        "< 10"     || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
    }


    def "try to update enrollment after deadline"() {
        given:
        institution.getActivities() >> []
        theme.getState() >> Theme.State.APPROVED

        def activityDtoTwo
        def themes = [theme]
        activityDtoTwo = new ActivityDto()
        activityDtoTwo.name = SpockTest.ACTIVITY_NAME_1
        activityDtoTwo.region = SpockTest.ACTIVITY_REGION_1
        activityDtoTwo.participantsNumberLimit = 2
        activityDtoTwo.description = SpockTest.ACTIVITY_DESCRIPTION_1
        activityDtoTwo.startingDate = DateHandler.toISOString(SpockTest.IN_TWO_DAYS)
        activityDtoTwo.endingDate = DateHandler.toISOString(SpockTest.IN_THREE_DAYS)
        activityDtoTwo.applicationDeadline = DateHandler.toISOString(SpockTest.IN_ONE_DAY)
        
        activity2 = new Activity(activityDtoTwo, institution, themes)
        
        and: "enrollment"
        def enrollmentDtoTwo = new EnrollmentDto()
        enrollmentDtoTwo.motivation = SpockTest.ENROLLMENT_MOTIVATION_1
        enrollmentTwo = new Enrollment(activity2, volunteer, enrollmentDtoTwo)
        activity2.setApplicationDeadline(SpockTest.ONE_DAY_AGO)
        enrollmentDtoEdit.motivation = SpockTest.ENROLLMENT_MOTIVATION_2

        when:
        enrollmentTwo.update(enrollmentDtoEdit)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_AFTER_DEADLINE
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}