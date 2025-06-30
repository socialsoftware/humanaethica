package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State
import spock.lang.Unroll
import java.time.LocalDateTime

@DataJpaTest
class UpdateEnrollmentServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def enrollment
    def activity
    def volunteer

    def setup() {
        def institution = institutionService.getDemoInstitution()

        given: "activity info"
        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1, 2, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS, null)
        and: "an activity"
        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, State.APPROVED)
        and: "an enrollment"
        enrollment = createEnrollment(activity, volunteer, SpockTest.ENROLLMENT_MOTIVATION_1)
    }


    def 'update enrollment' () {
        given: "an enrollment dto"
        def editedEnrollmentDto = new EnrollmentDto()
        editedEnrollmentDto.motivation = SpockTest.ENROLLMENT_MOTIVATION_2

        when:
        def result = enrollmentService.updateEnrollment(enrollment.id, editedEnrollmentDto)
        sleep(1)

        then: "the returned data is correct"
        result.motivation == SpockTest.ENROLLMENT_MOTIVATION_2
        and: "the enrollment  is stored"
        enrollmentRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedEnrollment = enrollmentRepository.findAll().get(0)
        storedEnrollment.motivation == SpockTest.ENROLLMENT_MOTIVATION_2
        storedEnrollment.enrollmentDateTime.isBefore(LocalDateTime.now())
        storedEnrollment.activity.id == activity.id
        storedEnrollment.volunteer.id == volunteer.id
    }    

    @Unroll
    def 'invalid arguments: motivation=#motivation | enrollmentId=#enrollmentId'() {
        given: "an enrollment dto"
        def editedEnrollmentDto = new EnrollmentDto()
        editedEnrollmentDto.motivation = motivation

        when:
        enrollmentService.updateEnrollment(getEnrollmentId(enrollmentId), editedEnrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "the enrollment is in the database"
        enrollmentRepository.findAll().size() == 1

        where:
        motivation                  | enrollmentId || errorMessage
        null                        | EXIST        || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        "  "                        | EXIST        || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
        SpockTest.ENROLLMENT_MOTIVATION_1 | null     || ErrorMessage.ENROLLMENT_NOT_FOUND
        SpockTest.ENROLLMENT_MOTIVATION_1 | NO_EXIST || ErrorMessage.ENROLLMENT_NOT_FOUND
    }

    def getEnrollmentId(enrollmentId){
        if(enrollmentId == EXIST)
            return enrollment.id
        else if (enrollmentId == NO_EXIST)
            return 222
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
