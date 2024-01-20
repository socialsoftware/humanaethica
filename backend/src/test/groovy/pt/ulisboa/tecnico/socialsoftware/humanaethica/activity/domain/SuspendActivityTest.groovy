package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class SuspendActivityTest extends SpockTest {
    Institution institution = Mock()
    Theme theme = Mock()
    Activity activity
    def activityDto

    def setup() {
        given: "activityDto"
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
    }

    @Unroll
    def "suspend activity with: state:#state"() {
        given: "activity"
        institution.getActivities() >> []
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]
        activity = new Activity(activityDto, institution, themes)
        activity.setState(state)

        when:
        activity.suspend()

        then: "it is suspended"
        activity.getState() == resultState

        where:
        state                   || resultState
        Activity.State.APPROVED || Activity.State.SUSPENDED
        Activity.State.REPORTED || Activity.State.SUSPENDED
    }

    @Unroll
    def "suspend suspended activity"() {
        given:
        institution.getActivities() >> []
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]
        activity = new Activity(activityDto, institution, themes)
        activity.setState(Activity.State.SUSPENDED)

        when:
        activity.suspend()

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_SUSPENDED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}