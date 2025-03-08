package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateActivitySuggestionServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def institution
    def volunteer

    def setup() {
        institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
    }

    def "create activity suggestion"() {
        given: "an activity suggestion dto"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS, IN_TEN_DAYS, IN_TWELVE_DAYS)

        when:
        def result = activitySuggestionService.createActivitySuggestion(volunteer.id, institution.id, activitySuggestionDto)

        then: "the returned data is correct"
        result.name == ACTIVITY_NAME_1
        result.description == ACTIVITY_DESCRIPTION_1
        result.region == ACTIVITY_REGION_1
        result.participantsNumberLimit == 1
        result.applicationDeadline == DateHandler.toISOString(IN_EIGHT_DAYS)
        result.startingDate == DateHandler.toISOString(IN_TEN_DAYS)
        result.endingDate == DateHandler.toISOString(IN_TWELVE_DAYS)
        result.getState() == ActivitySuggestion.State.IN_REVIEW.name()
        result.institutionId == institution.id
        result.volunteerId == volunteer.id
        and: "the activity suggestion is saved in the database"
        activitySuggestionRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedActivitySuggestion = activitySuggestionRepository.findById(result.id).get()
        storedActivitySuggestion.name == ACTIVITY_NAME_1
        storedActivitySuggestion.description == ACTIVITY_DESCRIPTION_1
        storedActivitySuggestion.region == ACTIVITY_REGION_1
        storedActivitySuggestion.participantsNumberLimit == 1
        storedActivitySuggestion.applicationDeadline == IN_EIGHT_DAYS
        storedActivitySuggestion.startingDate == IN_TEN_DAYS
        storedActivitySuggestion.endingDate == IN_TWELVE_DAYS
        storedActivitySuggestion.getState() == ActivitySuggestion.State.IN_REVIEW
        storedActivitySuggestion.institution.id == institution.id
        storedActivitySuggestion.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: description=#description | userId=#userId | institutionId=#institutionId'() {
        given: "an activity dto"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, description,
                IN_EIGHT_DAYS, IN_TEN_DAYS, IN_TWELVE_DAYS)

        when:
        activitySuggestionService.createActivitySuggestion(getObjectId(userId, volunteer), getObjectId(institutionId, institution), activitySuggestionDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity suggestion is stored in the database"
        activitySuggestionRepository.findAll().size() == 0

        where:
        description            | userId   | institutionId || errorMessage
        null                   | EXIST    | EXIST         || ErrorMessage.ACTIVITY_SUGGESTION_DESCRIPTION_INVALID
        ACTIVITY_DESCRIPTION_1 | null     | EXIST         || ErrorMessage.USER_NOT_FOUND
        ACTIVITY_DESCRIPTION_1 | NO_EXIST | EXIST         || ErrorMessage.USER_NOT_FOUND
        ACTIVITY_DESCRIPTION_1 | EXIST    | null          || ErrorMessage.INSTITUTION_NOT_FOUND
        ACTIVITY_DESCRIPTION_1 | EXIST    | NO_EXIST      || ErrorMessage.INSTITUTION_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}