package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.checkerframework.checker.units.qual.A
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateAssessmentServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def volunteer
    def institution

    def setup() {
        institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO,ONE_DAY_AGO,NOW,null)

        def activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
    }

    def 'create assessment' () {
        given:
        def assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1

        when:
        def result = assessmentService.createAssessment(volunteer.id, institution.id, assessmentDto)

        then:
        result.review == ASSESSMENT_REVIEW_1
        result.reviewDate != null
        and:
        assessmentRepository.findAll().size() == 1
        def storedAssessment = assessmentRepository.findAll().get(0)
        storedAssessment.review == ASSESSMENT_REVIEW_1
        storedAssessment.reviewDate.isBefore(DateHandler.now())
        storedAssessment.institution.id == institution.id
        storedAssessment.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | institutionId=#institutionId'() {
        given:
        def assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1

        when:
        assessmentService.createAssessment(getVolunteerId(volunteerId), getInstitutionId(institutionId), getAssessmentDto(assessmentValue,assessmentDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        assessmentRepository.findAll().size() == 0

        where:
        volunteerId | institutionId | assessmentValue || errorMessage
        null        | EXIST         | EXIST           || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST         | EXIST           || ErrorMessage.USER_NOT_FOUND
        EXIST       | null          | EXIST           || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST       | NO_EXIST      | EXIST           || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST       | EXIST         | null            || ErrorMessage.ASSESSMENT_REQUIRES_REVIEW
    }

    def getVolunteerId(volunteerId) {
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        else
            return null
    }

    def getInstitutionId(institutionId) {
        if (institutionId == EXIST)
            return institution.id
        else if (institutionId == NO_EXIST)
            return 222
        else
            return null
    }

    def getAssessmentDto(value, assessmentDto) {
        if (value == EXIST) {
            return assessmentDto
        }
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}