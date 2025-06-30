package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State
@DataJpaTest
class GetAssessmentsByInstitutionServiceTest extends SpockTest {
    def institution
    def otherInstitution

    def setup() {
        institution = institutionService.getDemoInstitution()

        otherInstitution = createInstitution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO, ONE_DAY_AGO,NOW,null)

        def activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def otherActivity = new Activity(activityDto, otherInstitution, new ArrayList<>())
        activityRepository.save(otherActivity)
    }

    def "get two assessments of the same institution"() {
        given:
        def volunteerOne = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, State.APPROVED)
        def volunteerTwo = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, State.APPROVED)
        and:
        createAssessment(institution, volunteerOne, ASSESSMENT_REVIEW_1)
        createAssessment(institution, volunteerTwo, ASSESSMENT_REVIEW_2)

        when:
        def assessments = assessmentService.getAssessmentsByInstitution(institution.id)

        then:
        assessments.size() == 2
        assessments.get(0).review == ASSESSMENT_REVIEW_1
        assessments.get(1).review == ASSESSMENT_REVIEW_2
    }

    def "get one assessment of an institution"() {
        given:
        def volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, State.APPROVED)
        and:
        createAssessment(institution, volunteer, ASSESSMENT_REVIEW_1)
        createAssessment(otherInstitution, volunteer, ASSESSMENT_REVIEW_2)

        when:
        def assessments = assessmentService.getAssessmentsByInstitution(institution.id)

        then:
        assessments.size() == 1
        assessments.get(0).review == ASSESSMENT_REVIEW_1
    }

    def "institution does not exist or is null: institutionId=#institutionId"() {
        when:
        assessmentService.getAssessmentsByInstitution(institutionId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_NOT_FOUND

        where:
        institutionId << [null, 222]
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
