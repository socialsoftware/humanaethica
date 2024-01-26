package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.application.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.application.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateEnrollmentMethodTest extends SpockTest {
    static final String THIS_VOLUNTEER = "THIS"
    static final String OTHER_VOLUNTEER = "OTHER"
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Volunteer otherVolunteer = Mock()
    Enrollment otherEnrollment = Mock()
    def enrolmentDto

    def setup() {
        given: "enrolment info"
        enrolmentDto = new EnrollmentDto()
        enrolmentDto.motivation = ENROLLMENT_MOTIVATION_1
    }

    def "create enrollment"() {
        given:
        activity.getEnrollments() >> [otherEnrollment]
        activity.getApplicationDeadline() >> IN_ONE_DAY
        otherEnrollment.getVolunteer() >> otherVolunteer

        when:
        def result = new Enrollment(activity, volunteer, enrolmentDto)

        then: "checks results"
        result.motivation == ENROLLMENT_MOTIVATION_1
        result.enrollmentDateTime.isBefore(LocalDateTime.now())
        result.activity == activity
        result.volunteer == volunteer
        and: "check that it is added"
        1 * activity.addEnrollment(_)
        1 * volunteer.addEnrollment(_)
    }

    @Unroll
    def "create enrollment and violate invariants: motivation=#motivation | enrollmentDateTime=#enrolmentDateTime | volunteerParam=#volunteerParam"() {
        given:
        activity.getEnrollments() >> [otherEnrollment]
        activity.getApplicationDeadline() >> enrolmentDateTime
        otherEnrollment.getVolunteer() >> getVolunteer(volunteerParam)
        and:
        enrolmentDto.motivation = motivation

        when:
        new Enrollment(activity, volunteer, enrolmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        motivation              | enrolmentDateTime | volunteerParam  || errorMessage
        null                    | IN_ONE_DAY        | OTHER_VOLUNTEER || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        "   "                   | IN_ONE_DAY        | OTHER_VOLUNTEER || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        "< 10"                  | IN_ONE_DAY        | OTHER_VOLUNTEER || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        ENROLLMENT_MOTIVATION_1 | ONE_DAY_AGO       | OTHER_VOLUNTEER || ErrorMessage.ENROLLMENT_AFTER_DEADLINE
        ENROLLMENT_MOTIVATION_1 | IN_ONE_DAY        | THIS_VOLUNTEER  || ErrorMessage.ENROLLMENT_VOLUNTEER_IS_ALREADY_ENROLLED
    }

    def getVolunteer(value) {
        return value == OTHER_VOLUNTEER ? otherVolunteer : volunteer
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}