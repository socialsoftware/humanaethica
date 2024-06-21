package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateAssessmentMethodTest extends SpockTest {
    Institution institution = Mock()
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Volunteer otherVolunteer = Mock()
    Assessment otherAssessment = Mock()
    def assessmentDto

    def setup() {
        given: "enrolment info"
        assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
    }

    def "create assessment"() {
        given:
        institution.getActivities() >> [activity]
        institution.getAssessments() >> [otherAssessment]
        activity.getEndingDate() >> ONE_DAY_AGO
        otherAssessment.getVolunteer() >> otherVolunteer

        when:
        def result = new Assessment(institution, volunteer, assessmentDto)

        then: "checks results"
        result.review == ASSESSMENT_REVIEW_1
        result.reviewDate.isBefore(DateHandler.now())
        result.institution == institution
        result.volunteer == volunteer
        and: "check that it is added"
        1 * institution.addAssessment(_)
        1 * volunteer.addAssessment(_)
    }

    @Unroll
    def "create assessment and violate review is required invariant: review=#review"() {
        given:
        institution.getActivities() >> [activity]
        institution.getAssessments() >> [otherAssessment]
        activity.getEndingDate() >> ONE_DAY_AGO
        otherAssessment.getVolunteer() >> otherVolunteer
        and:
        assessmentDto.review = review

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        review || errorMessage
        "<10" || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        "                   " || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        null || ErrorMessage.ASSESSMENT_REQUIRES_REVIEW
    }

    def "create assessment and violate assessment only if there are finished a activity invariant"() {
        given:
        institution.getActivities() >> [activity]
        institution.getAssessments() >> [otherAssessment]
        activity.getEndingDate() >> IN_TWO_DAYS
        otherAssessment.getVolunteer() >> otherVolunteer

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_ONLY_IF_INSTITUTION_HAS_FINISHED_ACTIVITIES
    }

    def "create assessment and violate assess only once invariant"() {
        given:
        institution.getActivities() >> [activity]
        institution.getAssessments() >> [otherAssessment]
        activity.getEndingDate() >> ONE_DAY_AGO
        otherAssessment.getVolunteer() >> volunteer

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_VOLUNTEER_CAN_ASSESS_INSTITUTION_ONLY_ONCE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}