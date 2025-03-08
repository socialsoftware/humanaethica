package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class GetActivitySuggestionServiceTest extends SpockTest {
    def institution

    def setup() {
        institution = institutionService.getDemoInstitution()
        def volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        given: "activity suggestion info"
        def activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS,IN_TEN_DAYS,IN_TWELVE_DAYS)
        and: "an activity suggestion"
        def activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)
        and: 'another activity'
        activitySuggestionDto.name = ACTIVITY_NAME_2
        activitySuggestion = new ActivitySuggestion(institution, volunteer, activitySuggestionDto)
        activitySuggestionRepository.save(activitySuggestion)
    }

    def 'get two activities'() {
        when:
        def result = activitySuggestionService.getActivitySuggestionsByInstitution(institution.id)

        then:
        result.size() == 2
        result.get(0).name == ACTIVITY_NAME_1
        result.get(1).name == ACTIVITY_NAME_2
    }

    def 'invalid arguments: institutionId=#institutionId'() {
        when:
        activitySuggestionService.getActivitySuggestionsByInstitution(getObjectId(institutionId, institution))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        institutionId || errorMessage
        null          || ErrorMessage.INSTITUTION_NOT_FOUND
        NO_EXIST      || ErrorMessage.INSTITUTION_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}