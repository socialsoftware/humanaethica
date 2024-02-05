package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class SuspendActivityMethodTest extends SpockTest {
    Institution institution = Mock()
    Activity activity
    def activityDto

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
    }

    @Unroll
    def "suspend activity with: state:#state"() {
        given: "activity"
        institution.getActivities() >> []
        def themes = []
        activity = new Activity(activityDto, institution, themes)
        activity.setState(state)

        when:
        activity.suspend()

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
        activity.suspend()

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_SUSPENDED
        activity.getState() == Activity.State.SUSPENDED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}