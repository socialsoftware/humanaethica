package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import spock.lang.Unroll


@DataJpaTest
class DeleteEnrollmentServiceTest extends SpockTest {
    public static final String FIRST_ENROLLMENT = 'ENROLLMENT_1'
    public static final String SECOND_ENROLLMENT = 'ENROLLMENT_2'

    def volunteer
    def activity
    def enrollment
    def firstEnrollment
    def secondEnrollment

    def setup() {
        def institution = institutionService.getDemoInstitution()

        given: "activity info"
        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1, 2, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS, null)
        
        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.APPROVED)
        and: "enrollment"
        enrollment = createEnrollment(activity, volunteer, SpockTest.ENROLLMENT_MOTIVATION_1)
    }

    def 'delete enrollment'() {
        given:
        firstEnrollment = enrollmentRepository.findAll().get(0)
        when:
        enrollmentService.removeEnrollment(firstEnrollment.id)
        then: "check that enrollment was deleted"
        enrollmentRepository.findAll().size() == 0

    }

    @Unroll
    def 'two enrollments exist and one is removed'() {
        
        given:
        def volunteer2 = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.State.APPROVED)
        def enrollment2 = createEnrollment(activity, volunteer2, SpockTest.ENROLLMENT_MOTIVATION_2)
        enrollmentRepository.save(enrollment2)
        firstEnrollment = enrollmentRepository.findAll().get(0)
        secondEnrollment = enrollmentRepository.findAll().get(1)

        when:
        def result = enrollmentService.removeEnrollment(getFirstOrSecondEnrollment(enrollmentId))
    
        then: "the enrollment was deleted"
        enrollmentRepository.findAll().size() == 1
        result.motivation == removedMotivation 
        def remainingEnrollment = enrollmentRepository.findAll().get(0)
        remainingEnrollment.motivation == remainingMotivation

        where: "check the motivation of the remainingEnrollment and of the removedEnrollment"
        enrollmentId        || removedMotivation        || remainingMotivation
        FIRST_ENROLLMENT    || SpockTest.ENROLLMENT_MOTIVATION_1 || SpockTest.ENROLLMENT_MOTIVATION_2
        SECOND_ENROLLMENT   || SpockTest.ENROLLMENT_MOTIVATION_2 || SpockTest.ENROLLMENT_MOTIVATION_1

    }

    def 'two enrollments exist and are both deleted'() {
        given:
        def volunteer2 = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.State.APPROVED)
        def enrollment2 = createEnrollment(activity, volunteer2, SpockTest.ENROLLMENT_MOTIVATION_2)
        enrollmentRepository.save(enrollment2)
        firstEnrollment = enrollmentRepository.findAll().get(0)
        secondEnrollment = enrollmentRepository.findAll().get(1)

        when:
        enrollmentService.removeEnrollment(firstEnrollment.id)
        enrollmentService.removeEnrollment(secondEnrollment.id)

        then: "confirm that enrollments were removed"
        enrollmentRepository.findAll().size() == 0
    }

    @Unroll
    def 'invalid arguments: enrollmentId=#enrollmentId'() {

        when:
        enrollmentService.removeEnrollment(enrollmentId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "the enrollment is in the database"
        enrollmentRepository.findAll().size() == 1

        where:
        enrollmentId   ||  errorMessage
        null           ||  ErrorMessage.ENROLLMENT_NOT_FOUND
        222            ||  ErrorMessage.ENROLLMENT_NOT_FOUND

    }

    def getFirstOrSecondEnrollment(enrollmentId) {
    if(enrollmentId == FIRST_ENROLLMENT)
        return firstEnrollment.id
    else if (enrollmentId == SECOND_ENROLLMENT)
        return secondEnrollment.id
    return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}