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
class DeleteEnrollmentMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Enrollment otherEnrollment = Mock()
    def enrolment
    def enrollmentDtoOne

    def setup() {
        given:
        enrollmentDtoOne = new EnrollmentDto()
        enrollmentDtoOne.motivation = ENROLLMENT_MOTIVATION_1
        enrollmentDtoOne.enrollmentDateTime = DateHandler.toISOString(TWO_DAYS_AGO)

        and: "enrollment"
        otherEnrollment.getMotivation() >> ENROLLMENT_MOTIVATION_2 
        activity.getEnrollments() >> [otherEnrollment]

        enrollment = new Enrollment(activity, volunteer, EnrollmentDtoOne)
    }


    def "delete enrollment"() {

        when: "enrollment is deleted"
        volunteer.removeEnrollment(enrollment)
        activity.removeEnrollment(enrollment)
        enrollment.delete()

        then: "checks if the enrollment was deleted in the activtiy and volunteer"
        volunteer.getEnrollments().size() == 0
        activity.getEnrollments().size() == 0

    }
    
    def "try to delete enrollment after deadline"() {
        given:
        activity.getApplicationDeadline() >> ONE_DAY_AGO

        when:
        enrollment.delete()

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_AFTER_DEADLINE
    }
    
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}