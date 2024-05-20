package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
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
        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 2, ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS, IN_THREE_DAYS, null)
        
        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and: "enrollment"
        enrollment = createEnrollment(activity, volunteer, ENROLLMENT_MOTIVATION_1)
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
        def volunteer2 = createVolunteer(USER_2_NAME, USER_2_PASSWORD, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def enrollment2 = createEnrollment(activity, volunteer2, ENROLLMENT_MOTIVATION_2)
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
        FIRST_ENROLLMENT    || ENROLLMENT_MOTIVATION_1  || ENROLLMENT_MOTIVATION_2
        SECOND_ENROLLMENT   || ENROLLMENT_MOTIVATION_2  || ENROLLMENT_MOTIVATION_1

    }

    def 'two enrollments exist and are both deleted'() {
        given:
        def volunteer2 = createVolunteer(USER_2_NAME, USER_2_PASSWORD, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def enrollment2 = createEnrollment(activity, volunteer2, ENROLLMENT_MOTIVATION_2)
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