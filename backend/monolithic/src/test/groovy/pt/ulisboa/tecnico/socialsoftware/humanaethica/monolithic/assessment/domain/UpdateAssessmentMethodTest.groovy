package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import spock.lang.Unroll

@DataJpaTest
class UpdateAssessmentMethodTest extends SpockTest {
    Institution institution = Mock()
    Activity activity = Mock()
    Enrollment otherEnrollment = Mock()
    Theme theme = Mock()

    def assessmentDtoEdit

    def volunteer
    def assessment

    def setup() {
        given:
        activity.getEnrollments() >> [otherEnrollment]
        activity.getApplicationDeadline() >> IN_TWO_DAYS
        activity.getEndingDate() >> DateHandler.now()
        institution.getActivities() >> [activity]
        institution.getAssessments() >> []

        and: "volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.APPROVED)

        and: "an assessment"
        def assessmentDto
        assessmentDto = new AssessmentDto();
        assessmentDto.review = ASSESSMENT_REVIEW_1
        assessment = new Assessment(institution, volunteer, assessmentDto);

        and: "an assessment review edit"
        assessmentDtoEdit = new AssessmentDto();
        assessmentDtoEdit.review = ASSESSMENT_REVIEW_2
    }

    def "update assessment"() {
        given:
        assessment.review = ASSESSMENT_REVIEW_1

        when:
        assessment.update(assessmentDtoEdit);
        sleep(1)

        then: "checks results"
        assessment.getReview() == ASSESSMENT_REVIEW_2
        assessment.getReviewDate().isBefore(DateHandler.now())
    }

    @Unroll
    def 'invalid review message=#message'() {
        given:
        def assessmentDtoMessage = new AssessmentDto()
        assessmentDtoMessage.review = message

        when:
        assessment.update(assessmentDtoMessage)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        message || errorMessage
        "short" || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        " a d w"|| ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        null || ErrorMessage.ASSESSMENT_REQUIRES_REVIEW
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}