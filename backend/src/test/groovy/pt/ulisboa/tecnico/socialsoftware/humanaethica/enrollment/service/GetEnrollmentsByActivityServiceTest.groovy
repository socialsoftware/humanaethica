package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@DataJpaTest
class GetEnrollmentsByActivityServiceTest extends SpockTest {
    def activity
    def otherActivity

    def setup() {
        def institution = institutionService.getDemoInstitution()

        def activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 1
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)

        def themes = new ArrayList<>()

        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        activityDto.name = ACTIVITY_NAME_2
        otherActivity = new Activity(activityDto, institution, themes)
        activityRepository.save(otherActivity)
    }

    def "get two enrollments of the same activity"() {
        given:
        def volunteerOne = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def volunteerTwo = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createEnrollment(activity, volunteerOne, ENROLLMENT_MOTIVATION_1)
        createEnrollment(activity, volunteerTwo, ENROLLMENT_MOTIVATION_2)

        when:
        def enrollments = enrollmentService.getEnrollmentsByActivity(activity.id)

        then:
        enrollments.size() == 2
        enrollments.get(0).motivation == ENROLLMENT_MOTIVATION_1
        enrollments.get(1).motivation == ENROLLMENT_MOTIVATION_2
    }

    def "get one enrollments of an activity"() {
        given:
        def volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createEnrollment(activity, volunteer, ENROLLMENT_MOTIVATION_1)
        createEnrollment(otherActivity, volunteer, ENROLLMENT_MOTIVATION_2)

        when:
        def enrollments = enrollmentService.getEnrollmentsByActivity(activity.id)

        then:
        enrollments.size() == 1
        enrollments.get(0).motivation == ENROLLMENT_MOTIVATION_1
    }

    def createVolunteer(name, userName, email, type, state) {
        def volunteer = new Volunteer(name, userName, email, type, state)
        userRepository.save(volunteer)
        return volunteer
    }

    def createEnrollment(activity, volunteer, motivation) {
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(motivation)
        def enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)
        return enrollment
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
