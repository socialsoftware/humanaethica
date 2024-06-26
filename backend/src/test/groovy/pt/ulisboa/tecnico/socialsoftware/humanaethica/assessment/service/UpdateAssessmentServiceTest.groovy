package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentRepository

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll
import java.time.LocalDateTime

@DataJpaTest
class UpdateAssessmentServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def assessment
    def activity

    def setup() {
        def institution = institutionService.getDemoInstitution()

        given: "activity info"
        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 2, ACTIVITY_DESCRIPTION_1,
                THREE_DAYS_AGO, TWO_DAYS_AGO, ONE_DAY_AGO, null)

        and: "an activity"
        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        institution.addActivity(activity)

        and: "a volunteer"
        def volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        and: "an assessment"
        assessment = createAssessment(institution, volunteer, ASSESSMENT_REVIEW_1)
    }


    def 'update assessment' () {
        given: "an assessment dto"
        def editedAssessmentDto = new AssessmentDto()
        editedAssessmentDto.review = ASSESSMENT_REVIEW_2

        when:
        def result = assessmentService.updateAssessment(assessment.id, editedAssessmentDto)

        then: "the returned data is correct"
        result.review == ASSESSMENT_REVIEW_2
        and: "the assessment  is stored"
        assessmentRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedAssessment = assessmentRepository.findAll().get(0)
        storedAssessment.review == ASSESSMENT_REVIEW_2
        storedAssessment.reviewDate.isBefore(DateHandler.now())
    }

    @Unroll
    def 'invalid arguments: review=#review | assessmentId=#assessmentId'() {
        given:
        def editedAssessmentDto = new AssessmentDto()
        editedAssessmentDto.review = review

        when:
        def result = assessmentService.updateAssessment(getAssessmentId(assessmentId), editedAssessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        review                  | assessmentId          || errorMessage
        "too short"             | EXIST                 || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        "This is a bin review!" | null                  || ErrorMessage.ASSESSMENT_NOT_FOUND
        "This is a bin review!" | NO_EXIST              || ErrorMessage.ASSESSMENT_NOT_FOUND
    }

    def getAssessmentId(assessmentId) {
        if (assessmentId == EXIST)
            return assessment.id
        else if (assessmentId == NO_EXIST)
            return 222

        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}