package pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.domain.ActivitySuggestion
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activitysuggestion.dto.ActivitySuggestionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetActivitySuggestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institution

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "the demo institution and a volunteer"
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

    def "a member of an institution gets the activity suggestions for that institution"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/activitySuggestions/institution/' + institution.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ActivitySuggestionDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).name == ACTIVITY_NAME_1
        response.get(1).name == ACTIVITY_NAME_2
        response.get(1).region == ACTIVITY_REGION_1
        response.get(1).participantsNumberLimit == 1
        response.get(1).description == ACTIVITY_DESCRIPTION_1
        DateHandler.toLocalDateTime(response.get(1).applicationDeadline).withNano(0) == IN_EIGHT_DAYS.withNano(0)
        DateHandler.toLocalDateTime(response.get(1).startingDate).withNano(0) == IN_TEN_DAYS.withNano(0)
        DateHandler.toLocalDateTime(response.get(1).endingDate).withNano(0) == IN_TWELVE_DAYS.withNano(0)

        cleanup:
        deleteAll()
    }

    def "a member of another institution gets the activity suggestions for that institution"() {
        given:
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        createMember(USER_2_NAME,USER_2_USERNAME,USER_2_PASSWORD,USER_2_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

        when:
        webClient.get()
                .uri('/activitySuggestions/institution/' + institution.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ActivitySuggestionDto.class)
                .collectList()
                .block()
        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }
}