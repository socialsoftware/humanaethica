package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        def themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,false,false,false))

        def activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumber(2)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setEndingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(NOW))
        activityDto.setThemes(themes)
        def activity = activityService.registerActivity(user.id, activityDto)

        activityId = activity.id
        activityService.reportActivity(activityId)
    }

    def "admin validates activity"() {
        given: "login admin"
        demoAdminLogin()

        when: 'validate'
        def response = webClient.put()
                .uri('/activity/' + activityId + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response"
        response.state == Activity.State.APPROVED.name()
        and: 'database'
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }

    def "admin validates activity with wrong id"() {
        given:
        demoAdminLogin()

        when:
        webClient.put()
                .uri('/activity/' + '222' + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "error"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.REPORTED
    }

    def "member tries to validate activity"() {
        given:
        demoMemberLogin()

        when:
        webClient.put()
                .uri('/activity/' + activityId + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "error is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.REPORTED
    }

    def "volunteer tries to validate activity"() {
        given:
        demoVolunteerLogin()

        when:
        webClient.put()
                .uri('/activity/' + activityId + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "error is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.REPORTED
    }
}