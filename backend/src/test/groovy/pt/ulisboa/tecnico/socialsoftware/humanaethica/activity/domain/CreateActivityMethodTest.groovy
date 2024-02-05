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
    Activity otherActivity = Mock()
    def activityDto

    def setup() {
        given: "activity info"
        activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumberLimit = 2
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
    }

    def "create activity with theme and institution has another activity"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]

        when:
        def result = new Activity(activityDto, institution, themes)

        then: "check result"
        result.getInstitution() == institution
        result.getName() == ACTIVITY_NAME_1
        result.getRegion() == ACTIVITY_REGION_1
        result.getParticipantsNumberLimit() == 2
        result.getDescription() == ACTIVITY_DESCRIPTION_1
        result.getStartingDate() == IN_TWO_DAYS
        result.getEndingDate() == IN_THREE_DAYS
        result.getApplicationDeadline() == IN_ONE_DAY
        result.getThemes().size() == 1
        result.getThemes().get(0) == theme
        and: "invocations"
        1 * institution.addActivity(_)
        1 * theme.addActivity(_)
    }

    @Unroll
    def "create activity and violate invariants name, region, and description are required: name=#name | region=#region | description=#description"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]
        and: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setParticipantsNumberLimit(1)
        activityDto.setDescription(description)
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))

        when:
        new Activity(activityDto, institution, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        name            | region            | description            || errorMessage
        null            | ACTIVITY_REGION_1 | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_NAME_INVALID
        " "             | ACTIVITY_REGION_1 | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_NAME_INVALID
        ACTIVITY_NAME_1 | null              | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | " "               | ACTIVITY_DESCRIPTION_1 || ErrorMessage.ACTIVITY_REGION_NAME_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | null                   || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
        ACTIVITY_NAME_1 | ACTIVITY_REGION_1 | "  "                   || ErrorMessage.ACTIVITY_DESCRIPTION_INVALID
    }

    @Unroll
    def "create activity and violate has 1 to 5 participants : participants=#participants"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]
        and: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumberLimit(participants)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))

        when:
        new Activity(activityDto, institution, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SHOULD_HAVE_ONE_TO_FIVE_PARTICIPANTS

        where:
        participants << [null, -5, 0, 6, 10]
      }

    @Unroll
    def "create activity and violate dates are required invariant: deadline=#deadline | start=#start | end=#end"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]
        and: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumberLimit(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setApplicationDeadline(deadline instanceof LocalDateTime ? DateHandler.toISOString(deadline) : deadline as String)
        activityDto.setStartingDate(start instanceof LocalDateTime ? DateHandler.toISOString(start) : start as String)
        activityDto.setEndingDate(end instanceof LocalDateTime ? DateHandler.toISOString(end) : end as String)

        when:
        new Activity(activityDto, institution, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        deadline     | start                 | end           || errorMessage
        null         | IN_TWO_DAYS           | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        "  "         | IN_TWO_DAYS           | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        "2024-01-12" | IN_TWO_DAYS           | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        IN_ONE_DAY   | null                  | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        IN_ONE_DAY   | "   "                 | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        IN_ONE_DAY   | "2024-01-12 12:00:00" | IN_THREE_DAYS || ErrorMessage.ACTIVITY_INVALID_DATE
        IN_ONE_DAY   | IN_TWO_DAYS           | null          || ErrorMessage.ACTIVITY_INVALID_DATE
        IN_ONE_DAY   | IN_TWO_DAYS           | "     "       || ErrorMessage.ACTIVITY_INVALID_DATE
        IN_ONE_DAY   | IN_TWO_DAYS           | "12:00:00"    || ErrorMessage.ACTIVITY_INVALID_DATE
     }

    @Unroll
    def "create activity violate date precedence invariants: deadline=#deadline | start=#start | end=#end"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]
        and: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumberLimit(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setApplicationDeadline(deadline instanceof LocalDateTime ? DateHandler.toISOString(deadline) : deadline as String)
        activityDto.setStartingDate(start instanceof LocalDateTime ? DateHandler.toISOString(start) : start as String)
        activityDto.setEndingDate(end instanceof LocalDateTime ? DateHandler.toISOString(end) : end as String)

        when:
        new Activity(activityDto, institution, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        deadline   | start       | end           || errorMessage
        IN_ONE_DAY | NOW         | IN_THREE_DAYS || ErrorMessage.ACTIVITY_APPLICATION_DEADLINE_AFTER_START
        IN_ONE_DAY | IN_ONE_DAY  | IN_THREE_DAYS || ErrorMessage.ACTIVITY_APPLICATION_DEADLINE_AFTER_START
        IN_ONE_DAY | IN_TWO_DAYS | NOW           || ErrorMessage.ACTIVITY_START_AFTER_END
        IN_ONE_DAY | IN_TWO_DAYS | IN_TWO_DAYS   || ErrorMessage.ACTIVITY_START_AFTER_END
     }

    @Unroll
    def "create activity and violate themes are approved invariant: themeStatus=#themeStatus"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]
        theme.getState() >> themeStatus
        def themes = [theme]
        and: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumberLimit(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))

        when:
        new Activity(activityDto, institution, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        themeStatus           || errorMessage
        Theme.State.DELETED   || ErrorMessage.THEME_NOT_APPROVED
        Theme.State.SUBMITTED || ErrorMessage.THEME_NOT_APPROVED
    }

    @Unroll
    def "create activity violate unique name invariant"() {
        given:
        otherActivity.getName() >> ACTIVITY_NAME_2
        institution.getActivities() >> [otherActivity]
        theme.getState() >> Theme.State.APPROVED
        def themes = [theme]
        and: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_2)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumberLimit(1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))

        when:
        new Activity(activityDto, institution, themes)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}