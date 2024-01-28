package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
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


        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
    }

    def 'create enrollment' () {
        given:
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_1

        when:
        def result = enrollmentService.createEnrollment(volunteer.id, activity.id, enrollmentDto)

        then:
        result.motivation == ENROLLMENT_MOTIVATION_1
        and:
        enrollmentRepository.findAll().size() == 1
        def storedEnrollment = enrollmentRepository.findAll().get(0)
        storedEnrollment.motivation == ENROLLMENT_MOTIVATION_1
        storedEnrollment.activity.id == activity.id
        storedEnrollment.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | activityId=#activityId'() {
        given:
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_1

        when:
        def result = enrollmentService.createEnrollment(getVolunteerId(volunteerId), getActivityId(activityId), enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        enrollmentRepository.findAll().size() == 0

        where:
        volunteerId | activityId || errorMessage
        null        | EXIST      || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST      || ErrorMessage.USER_NOT_FOUND
        EXIST       | null       || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | NO_EXIST   || ErrorMessage.ACTIVITY_NOT_FOUND
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

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
