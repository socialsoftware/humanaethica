package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler
import spock.lang.Unroll

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
        sleep(1)

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