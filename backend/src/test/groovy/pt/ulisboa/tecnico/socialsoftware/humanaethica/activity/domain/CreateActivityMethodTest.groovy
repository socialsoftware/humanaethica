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
class CreateActivityMethodTest extends SpockTest {
    Institution institution = Mock()
    Theme theme = Mock()
    Activity activity = Mock()
    def activityDto

    def setup() {
        given: "activity info"
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
    }

    def "create activity with theme and institution has another activity"() {
        given:
        activity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [activity]
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]

        when:
        def result = new Activity(activityDto, institution, themes)

        then: "checks if activity"
        result.getInstitution() == institution
        result.getName() == ACTIVITY_NAME_1
        result.getRegion() == ACTIVITY_REGION_1
        result.getParticipantsNumber() == 2
        result.getDescription() == ACTIVITY_DESCRIPTION_1
        result.getStartingDate() == IN_TWO_DAYS
        result.getEndingDate() == IN_THREE_DAYS
        result.getApplicationDeadline() == IN_ONE_DAY
        result.getThemes().size() == 1
        result.getThemes().get(0) == theme
    }

    @Unroll
    def "create activity and violate invariants for arguments: name=#name | region=#region | participants=#participants | description=#description | deadline=#deadline | start=#start | end=#end | themeStatus=#themeStatus"() {
        given:
        activity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [activity]
        theme.getState() >> themeStatus
        def themes = [theme]
        and: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setParticipantsNumber(participants)
        activityDto.setDescription(description)
        activityDto.setApplicationDeadline(deadline instanceof LocalDateTime ? DateHandler.toISOString(deadline) : deadline as String)
        activityDto.setStartingDate(start instanceof LocalDateTime ? DateHandler.toISOString(start) : start as String)
        activityDto.setEndingDate(end instanceof LocalDateTime ? DateHandler.toISOString(end) : end as String)

        when:
        new Activity(activityDto, institution, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        name            | region            | participants | description            | deadline   | start       | end           | themeStatus           || errorMessage
        null            | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_NAME_INVALID
        " "             | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_1 | null              | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | " "               | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | -5           | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 0            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 6            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 10           | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | null                   | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | "  "                   | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | null       | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | "  "       | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | null        | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | "   "       | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | null          | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | "     "       | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_INVALID_DATE
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | NOW         | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_APPLICATION_DEADLINE_AFTER_START
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_TWO_DAYS   | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_START_AFTER_END
        ACTIVITY_NAME_2 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.APPROVED  || ErrorMessage.ACTIVITY_ALREADY_EXISTS
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.DELETED   || ErrorMessage.THEME_NOT_APPROVED
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | 1            | ACTIVITY_DESCRIPTION_1 | IN_ONE_DAY | IN_TWO_DAYS | IN_THREE_DAYS | Theme.State.SUBMITTED || ErrorMessage.THEME_NOT_APPROVED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}