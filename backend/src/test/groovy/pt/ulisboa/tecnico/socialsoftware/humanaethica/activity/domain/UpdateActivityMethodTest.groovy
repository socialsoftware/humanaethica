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
class UpdateActivityMethodTest extends SpockTest {
    Institution institution = Mock()
    Theme themeOne = Mock()
    Theme themeTwo = Mock()
    Activity otherActivity = Mock()
    def activity
    def activityDtoOne
    def activityDtoTwo

    def setup() {
        given:
        activityDtoOne = new ActivityDto()
        activityDtoOne.name = ACTIVITY_NAME_1
        activityDtoOne.region = ACTIVITY_REGION_1
        activityDtoOne.participantsNumberLimit = 2
        activityDtoOne.description = ACTIVITY_DESCRIPTION_1
        activityDtoOne.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDtoOne.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDtoOne.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        and: "activity"
        otherActivity.getName() >> ACTIVITY_NAME_3
        institution.getActivities() >> [otherActivity]
        institution.getActivities() >> []
        themeOne.getState() >> Theme.State.APPROVED
        def themes = [themeOne]
        activity = new Activity(activityDtoOne, institution, themes)
        and:
        activityDtoTwo = new ActivityDto()
        activityDtoTwo.name = ACTIVITY_NAME_2
        activityDtoTwo.region = ACTIVITY_REGION_2
        activityDtoTwo.participantsNumberLimit = 4
        activityDtoTwo.description = ACTIVITY_DESCRIPTION_2
        activityDtoTwo.startingDate = DateHandler.toISOString(IN_ONE_DAY)
        activityDtoTwo.endingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDtoTwo.applicationDeadline = DateHandler.toISOString(NOW)
    }

    def "update activity"() {
        given:
        themeTwo.getState() >> Theme.State.APPROVED
        def themes = [themeTwo]

        when:
        activity.update(activityDtoTwo, themes)

        then: "checks if activity"
        activity.getInstitution() == institution
        activity.getName() == ACTIVITY_NAME_2
        activity.getRegion() == ACTIVITY_REGION_2
        activity.getParticipantsNumberLimit() == 4
        activity.getDescription() == ACTIVITY_DESCRIPTION_2
        activity.getStartingDate() == IN_ONE_DAY
        activity.getEndingDate() == IN_TWO_DAYS
        activity.getApplicationDeadline() == NOW
        activity.getThemes().size() == 1
        activity.getThemes().get(0) == themeTwo
    }

    @Unroll
    def "create activity and violate invariants for arguments: name=#name | region=#region | participants=#participants | description=#description | deadline=#deadline | start=#start | end=#end | themeStatus=#themeStatus"() {
        given:
        themeTwo.getState() >> themeStatus
        def themes = [themeTwo]
        and: "an activity dto"
        activityDtoTwo = new ActivityDto()
        activityDtoTwo.setName(name)
        activityDtoTwo.setRegion(region)
        activityDtoTwo.setParticipantsNumberLimit(participants)
        activityDtoTwo.setDescription(description)
        activityDtoTwo.setApplicationDeadline(deadline instanceof LocalDateTime ? DateHandler.toISOString(deadline) : deadline as String)
        activityDtoTwo.setStartingDate(start instanceof LocalDateTime ? DateHandler.toISOString(start) : start as String)
        activityDtoTwo.setEndingDate(end instanceof LocalDateTime ? DateHandler.toISOString(end) : end as String)

        when:
        activity.update(activityDtoTwo, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        name            | region            | participants | description            | deadline | start      | end         | themeStatus           || errorMessage
        null            | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_NAME_INVALID
        " "             | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_2 | null              | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_2 | " "               | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | -5           | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 0            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 6            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 20           | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | null                   | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | "  "                   | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | null     | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | "  "     | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | null       | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | "   "      | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | null        | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | "     "     | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | NOW        | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_APPLICATION_DEADLINE_AFTER_START
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_ONE_DAY  | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_START_AFTER_END
        ACTIVITY_NAME_3 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_ALREADY_EXISTS
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.DELETED   || ErrorMessage.THEME_NOT_APPROVED
        ACTIVITY_NAME_2 | ACTIVITY_REGION_2 | 2            | ACTIVITY_DESCRIPTION_2 | NOW      | IN_ONE_DAY | IN_TWO_DAYS | Theme.State.SUBMITTED || ErrorMessage.THEME_NOT_APPROVED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}