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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateActivitySuggestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def volunteer
    def institution
    def activitySuggestionDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        activitySuggestionDto = createActivitySuggestionDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                IN_EIGHT_DAYS,IN_TEN_DAYS,IN_TWELVE_DAYS)
    }

    def "login as demo volunteer and create an activity suggestion"() {
        given: "a volunteer"
        demoVolunteerLogin()

        when: "the volunteer suggests an activity"
        def response = webClient.post()
                .uri('/activitySuggestions/institution/' + institution.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activitySuggestionDto)
                .retrieve()
                .bodyToMono(ActivitySuggestionDto.class)
                .block()

        then: "check response data"
        response.name == ACTIVITY_NAME_1
        response.description == ACTIVITY_DESCRIPTION_1
        response.region == ACTIVITY_REGION_1
        response.participantsNumberLimit == 2
        response.applicationDeadline == DateHandler.toISOString(IN_EIGHT_DAYS)
        response.startingDate == DateHandler.toISOString(IN_TEN_DAYS)
        response.endingDate == DateHandler.toISOString(IN_TWELVE_DAYS)
        response.getState() == ActivitySuggestion.State.IN_REVIEW.name()
        response.institutionId == institution.id
        response.volunteerId == volunteer.id
        and: "the activity suggestion is saved in the database"
        activitySuggestionRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedActivitySuggestion = activitySuggestionRepository.findById(response.id).get()
        storedActivitySuggestion.name == ACTIVITY_NAME_1
        storedActivitySuggestion.description == ACTIVITY_DESCRIPTION_1
        storedActivitySuggestion.region == ACTIVITY_REGION_1
        storedActivitySuggestion.participantsNumberLimit == 2
        storedActivitySuggestion.applicationDeadline.withNano(0) == IN_EIGHT_DAYS.withNano(0)
        storedActivitySuggestion.startingDate.withNano(0) == IN_TEN_DAYS.withNano(0)
        storedActivitySuggestion.endingDate.withNano(0) == IN_TWELVE_DAYS.withNano(0)
        storedActivitySuggestion.state == ActivitySuggestion.State.IN_REVIEW
        storedActivitySuggestion.institution.id == institution.id
        storedActivitySuggestion.volunteer.id == volunteer.id

        cleanup:
        deleteAll()
    }

    def "login as volunteer and create an activity suggestion with error"() {
        given: "a volunteer"
        demoVolunteerLogin()
        and: "an invalid description used"
        activitySuggestionDto.description = "  "

        when: "the volunteer suggests an activity"
        webClient.post()
                .uri('/activitySuggestions/institution/' + institution.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activitySuggestionDto)
                .retrieve()
                .bodyToMono(ActivitySuggestionDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        activitySuggestionRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "members cannot suggest activities"() {
        given:
        demoMemberLogin()

        when: "an activity is suggested"
        webClient.post()
                .uri('/activitySuggestions/institution/' + institution.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activitySuggestionDto)
                .retrieve()
                .bodyToMono(ActivitySuggestionDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activitySuggestionRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "admin cannot suggest activities"() {
        given:
        demoAdminLogin()

        when: "an activity is suggested"
        webClient.post()
                .uri('/activitySuggestions/institution/' + institution.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activitySuggestionDto)
                .retrieve()
                .bodyToMono(ActivitySuggestionDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activitySuggestionRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "only logged in users can suggest activities"() {
        when: "an activity is suggested"
        webClient.post()
                .uri('/activitySuggestions/institution/' + institution.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activitySuggestionDto)
                .retrieve()
                .bodyToMono(ActivitySuggestionDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activitySuggestionRepository.count() == 0

        cleanup:
        deleteAll()
    }
}