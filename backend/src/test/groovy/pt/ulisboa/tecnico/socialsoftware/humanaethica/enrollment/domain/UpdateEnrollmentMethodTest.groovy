package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class UpdateEnrollmentMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Enrollment otherEnrollment = Mock()
    def enrollment
    def enrollmentDtoOne
    def enrollmentDtoEdit


    def setup() {
        given:
        enrollmentDtoOne = new EnrollmentDto()
        enrollmentDtoOne.motivation = ENROLLMENT_MOTIVATION_1
        activity.getEnrollments() >> [otherEnrollment]
        activity.getApplicationDeadline() >> IN_TWO_DAYS

        and: "enrollment"
        enrollment = new Enrollment(activity, volunteer, EnrollmentDtoOne)
        enrollmentDtoEdit = new EnrollmentDto()
    }


    def "update enrollment"() {
        given:
        enrollmentDtoEdit.motivation = ENROLLMENT_MOTIVATION_2

        when:
        enrollment.edit(enrollmentDtoEdit)

        then: "checks results"
        enrollment.getMotivation() == ENROLLMENT_MOTIVATION_2
        enrollment.enrollmentDateTime.isBefore(LocalDateTime.now())
        enrollment.activity == activity
        enrollment.volunteer == volunteer
        
    }



    @Unroll
    def "edit enrollment and violate motivation is required invariant: motivation=#motivation"() {
      given:
      enrollmentDtoEdit.motivation = motivation

      when:
      enrollment.edit(enrollmentDtoEdit)

      then:
      def error = thrown(HEException)
      error.getErrorMessage() == errorMessage

      where:
        motivation || errorMessage
        null       || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        "   "      || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        "< 10"     || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
    }
/*

    def "try to update enrollment after deadline"() {
        given:
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        and:
        enrollmentDtoEdit.motivation = ENROLLMENT_MOTIVATION_2

        when:
        enrollment.edit(enrollmentDtoEdit)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_AFTER_DEADLINE
    }*/


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}