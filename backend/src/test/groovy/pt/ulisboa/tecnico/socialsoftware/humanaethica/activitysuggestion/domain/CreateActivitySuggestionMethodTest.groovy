package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateActivitySuggestionMethodTest extends SpockTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    ActivitySuggestion otherActivitySuggestion = Mock()
    def activitySuggestionDto
    def dateBeforeTest

    def setup() {
        given: "activity info"
        activitySuggestionDto = new ActivitySuggestionDto()
        activitySuggestionDto.name = ACTIVITY_NAME_1
        activitySuggestionDto.description = ACTIVITY_DESCRIPTION_1
        activitySuggestionDto.region = ACTIVITY_REGION_1
        activitySuggestionDto.applicationDeadline = DateHandler.toISOString(IN_EIGHT_DAYS)
        activitySuggestionDto.startingDate = DateHandler.toISOString(IN_TEN_DAYS)
        activitySuggestionDto.endingDate = DateHandler.toISOString(IN_TWELVE_DAYS)
        activitySuggestionDto.participantsNumberLimit = 2
        and: "a date before tests run"
        dateBeforeTest = DateHandler.now()
    }

    def "create activity suggestion"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        when:
        def result = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then: "check result"
        result.getInstitution() == institution
        result.getVolunteer() == volunteer
        result.getName() == ACTIVITY_NAME_1
        result.getDescription() == ACTIVITY_DESCRIPTION_1
        result.getRegion() == ACTIVITY_REGION_1
        result.getCreationDate().isAfter(dateBeforeTest)
        result.getCreationDate().isBefore(DateHandler.now())
        result.getApplicationDeadline() == IN_EIGHT_DAYS
        result.getStartingDate() == IN_TEN_DAYS
        result.getEndingDate() == IN_TWELVE_DAYS
        result.getParticipantsNumberLimit() == 2
        and: "invocations"
        1 * institution.addActivitySuggestion(_)
        1 * volunteer.addActivitySuggestion(_)
    }

    @Unroll
    def "create activity suggestion and violate description invariant: description=#description"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]

        and: "an activity suggestion dto"
        activitySuggestionDto.setDescription(description)

        when:
        new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        description            || errorMessage
        null                   || ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_INVALID
        "  "                   || ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_INVALID
        "123456789"            || ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_INVALID
    }

    def "create activity suggestion that violates unique name invariant"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]
        and: "an activity suggestion dto"
        activitySuggestionDto.setName(ACTIVITY_NAME_2)

        when:
        new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_SUGGESTION_NAME_ALREADY_EXISTS
    }

    @Unroll
    def "create activity suggestion and violate application deadline invariant: deadline=#deadline"() {
        given:
        otherActivitySuggestion.getName() >> ACTIVITY_NAME_2
        volunteer.getActivitySuggestions() >> [otherActivitySuggestion]
        and: "an activity suggestion dto"
        activitySuggestionDto.setName(ACTIVITY_NAME_1)
        activitySuggestionDto.setApplicationDeadline(deadline instanceof LocalDateTime ? DateHandler.toISOString(deadline) : deadline as String)

        when:
        new ActivitySuggestion(institution, volunteer, activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        deadline     || errorMessage
        null         || ErrorMessage.ACTIVITY_SUGGESTION_APPLICATION_DEADLINE_TOO_SOON
        "  "         || ErrorMessage.ACTIVITY_SUGGESTION_APPLICATION_DEADLINE_TOO_SOON
        "2024-01-12" || ErrorMessage.ACTIVITY_SUGGESTION_APPLICATION_DEADLINE_TOO_SOON
        IN_ONE_DAY   || ErrorMessage.ACTIVITY_SUGGESTION_APPLICATION_DEADLINE_TOO_SOON
        IN_SIX_DAYS  || ErrorMessage.ACTIVITY_SUGGESTION_APPLICATION_DEADLINE_TOO_SOON
     }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}