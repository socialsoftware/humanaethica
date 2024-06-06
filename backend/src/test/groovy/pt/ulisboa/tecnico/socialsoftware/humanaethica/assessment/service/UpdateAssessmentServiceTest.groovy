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
                IN_ONE_DAY, IN_TWO_DAYS, IN_THREE_DAYS, null)

        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

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
        storedAssessment.assessmentDateTime.isBefore(LocalDateTime.now())
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}