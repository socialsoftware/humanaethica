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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.dto.ThemeDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportActivityWebServiceIT extends SpockTest {
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

    def "volunteer reports activity"() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.put()
                .uri('/activities/' + activityId + '/report')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response status"
        response.name == SpockTest.ACTIVITY_NAME_1
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.REPORTED
    }

    def "volunteer reports activity with wrong id"() {
        given:
        demoVolunteerLogin()

        when:
        webClient.put()
                .uri('/activities/' + '222' + '/report')
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

    def "member tries to report activity"() {
        given:
        demoMemberLogin()

        when:
        webClient.put()
                .uri('/activities/' + activityId + '/report')
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

    def "admin tries to report activity"() {
        given:
        demoAdminLogin()

        when:
        webClient.put()
                .uri('/activities/' + activityId + '/report')
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
}