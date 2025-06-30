package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State

@DataJpaTest
class SuspendActivityMethodTest extends SpockTest {
    Institution institution = Mock()
    Activity activity
    def activityDto
    def member

    def setup() {
        given: "activityDto"
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumberLimit = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)

        member = createMember(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, institution, State.ACTIVE)
    }

    @Unroll
    def "suspend activity with: state:#state"() {
        given: "activity"
        institution.getActivities() >> []
        def themes = []
        activity = new Activity(activityDto, institution, themes)
        activity.setState(state)

        when:
        activity.suspend(member.id, ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)

        then:
        activity.getState() == resultState

        where:
        state                   || resultState
        Activity.State.APPROVED || Activity.State.SUSPENDED
        Activity.State.REPORTED || Activity.State.SUSPENDED
    }

    @Unroll
    def "violate suspend precondition"() {
        given:
        institution.getActivities() >> []
        def themes = []
        activity = new Activity(activityDto, institution, themes)
        activity.setState(Activity.State.SUSPENDED)

        when:
        activity.suspend(member.id, ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_SUSPENDED
        activity.getState() == Activity.State.SUSPENDED
    }

    @Unroll
    def "suspend activity with an invalid justification:#justification"() {
        given: "activity"
        institution.getActivities() >> []
        def themes = []
        activity = new Activity(activityDto, institution, themes)

        when:
        activity.suspend(member.id, justification)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        justification           || errorMessage
        null                    || ErrorMessage.ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID
        "too short"             || ErrorMessage.ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID
        generateLongString()    || ErrorMessage.ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID
    }

    def "suspend activity after the ending date"() {
        given: "activity"
        institution.getActivities() >> []
        def themes = []
        activity = new Activity(activityDto, institution, themes)
        activity.setEndingDate(ONE_DAY_AGO)

        when:
        activity.suspend(member.id, ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUSPENSION_AFTER_END
    }

    def generateLongString(){
        return 'a'* 257
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}