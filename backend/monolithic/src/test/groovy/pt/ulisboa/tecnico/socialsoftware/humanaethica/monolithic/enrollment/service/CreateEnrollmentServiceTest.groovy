package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.exceptions.HEException
import spock.lang.Unroll

@DataJpaTest
class CreateEnrollmentServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def volunteer
    def activity

    def setup() {
        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
    }

    def 'create enrollment' () {
        given:
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = SpockTest.ENROLLMENT_MOTIVATION_1

        when:
        def result = enrollmentService.createEnrollment(volunteer.id, activity.id, enrollmentDto)

        then:
        result.motivation == SpockTest.ENROLLMENT_MOTIVATION_1
        and:
        enrollmentRepository.findAll().size() == 1
        def storedEnrollment = enrollmentRepository.findAll().get(0)
        storedEnrollment.motivation == SpockTest.ENROLLMENT_MOTIVATION_1
        storedEnrollment.activity.id == activity.id
        storedEnrollment.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | activityId=#activityId'() {
        given:
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = SpockTest.ENROLLMENT_MOTIVATION_1

        when:
        enrollmentService.createEnrollment(getVolunteerId(volunteerId), getActivityId(activityId), getEnrollmentDto(enrollmentValue,enrollmentDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        enrollmentRepository.findAll().size() == 0

        where:
        volunteerId | activityId | enrollmentValue || errorMessage
        null        | EXIST      | EXIST           || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST      | EXIST           || ErrorMessage.USER_NOT_FOUND
        EXIST       | null       | EXIST           || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | NO_EXIST   | EXIST           || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | EXIST      | null            || ErrorMessage.ENROLLMENT_REQUIRES_MOTIVATION
    }

    def getVolunteerId(volunteerId) {
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        else
            return null
    }

    def getActivityId(activityId) {
        if (activityId == EXIST)
            return activity.id
        else if (activityId == NO_EXIST)
            return 222
        else
            return null
    }

    def getEnrollmentDto(value, enrollmentDto) {
        if (value == EXIST) {
            return enrollmentDto
        }
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
