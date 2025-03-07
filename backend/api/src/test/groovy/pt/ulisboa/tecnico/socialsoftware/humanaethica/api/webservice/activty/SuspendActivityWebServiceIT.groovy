package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.activty

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SuspendActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()

        def theme = createTheme(SpockTest.THEME_NAME_1, Theme.State.APPROVED,null)
        def themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,2, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,themesDto)

        def activity = activityService.registerActivity(user.id, activityDto)

        activityId = activity.id
    }

    def "admin suspends activity"() {
        given:
        demoAdminLogin()

        when:
        def response = webClient.put()
                .uri('/activities/' + activityId + '/suspend/' + SpockTest.ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: 'check response'
        response.state == Activity.State.SUSPENDED.name()
        and: "check database data"
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.SUSPENDED
    }

    def "admin suspends activity with wrong id"() {
        given:
        demoAdminLogin()

        when:
        webClient.put()
                .uri('/activities/' + "222" + '/suspend/' + SpockTest.ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "error"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }

    def "volunteer tries to suspend activity"() {
        given: "login volunteer"
        demoVolunteerLogin()

        when:
        webClient.put()
                .uri('/activities/' + activityId + '/suspend/' + SpockTest.ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "error is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }

    def "member suspends activity"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.put()
                .uri('/activities/' + activityId + '/suspend/' + SpockTest.ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: 'check response'
        response.state == Activity.State.SUSPENDED.name()
        and: "check database data"
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.SUSPENDED
    }

    def "member not belonging to the activity tries to suspend it"() {
        given:
        def otherInstitution = new Institution(SpockTest.INSTITUTION_1_NAME, SpockTest.INSTITUTION_1_EMAIL, SpockTest.INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_PASSWORD, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(SpockTest.USER_1_USERNAME, SpockTest.USER_1_PASSWORD)

        when:
        webClient.put()
                .uri('/activities/' + activityId + '/suspend/' + SpockTest.ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def "member suspends activity with wrong id"() {
        given:
        demoMemberLogin()

        when:
        webClient.put()
                .uri('/activities/' + "222" + '/suspend/' + SpockTest.ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "error"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }
}