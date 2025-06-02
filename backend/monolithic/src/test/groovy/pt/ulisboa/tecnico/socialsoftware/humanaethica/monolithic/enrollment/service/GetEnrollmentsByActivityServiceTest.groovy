package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User

@DataJpaTest
class GetEnrollmentsByActivityServiceTest extends SpockTest {
    def activity
    def otherActivity

    def setup() {
        def institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        activityDto.name = SpockTest.ACTIVITY_NAME_2
        otherActivity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(otherActivity)
    }

    def "get two enrollments of the same activity"() {
        given:
        def volunteerOne = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.APPROVED)
        def volunteerTwo = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.State.APPROVED)
        and:
        createEnrollment(activity, volunteerOne, SpockTest.ENROLLMENT_MOTIVATION_1)
        createEnrollment(activity, volunteerTwo, SpockTest.ENROLLMENT_MOTIVATION_2)

        when:
        def enrollments = enrollmentService.getEnrollmentsByActivity(activity.id)

        then:
        enrollments.size() == 2
        enrollments.get(0).motivation == SpockTest.ENROLLMENT_MOTIVATION_1
        enrollments.get(1).motivation == SpockTest.ENROLLMENT_MOTIVATION_2
    }

    def "get one enrollment of an activity"() {
        given:
        def volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.APPROVED)
        and:
        createEnrollment(activity, volunteer, SpockTest.ENROLLMENT_MOTIVATION_1)
        createEnrollment(otherActivity, volunteer, SpockTest.ENROLLMENT_MOTIVATION_2)

        when:
        def enrollments = enrollmentService.getEnrollmentsByActivity(activity.id)

        then:
        enrollments.size() == 1
        enrollments.get(0).motivation == SpockTest.ENROLLMENT_MOTIVATION_1
    }

    def "activity does not exist or is null: activityId=#activityId"() {
        when:
        enrollmentService.getEnrollmentsByActivity(activityId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        activityId || errorMessage
        null       || ErrorMessage.ACTIVITY_NOT_FOUND
        222        || ErrorMessage.ACTIVITY_NOT_FOUND
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
