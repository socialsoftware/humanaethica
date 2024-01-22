package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterThemeWebServiceIT extends SpockTest {
    public static final String THEME_1__NAME = "THEME_1_NAME"
    @LocalServerPort
    private int port

    def themeDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        themeDto = new ThemeDto()
        themeDto.name = THEME_1__NAME
    }

    def "login as admin, and create a Theme"() {
        given: 'admin login'
        demoAdminLogin()

        when:
        webClient.post()
                .uri('/themes/register')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(themeDto)
                .retrieve()
                .toBodilessEntity()
                .block()

        then: "check database"
        themeRepository.count() == 1
        def theme = themeRepository.findAll().get(0)
        theme.name == THEME_1__NAME
        theme.state == Theme.State.APPROVED

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create a Theme"() {
        given: 'volunteer login'
        demoVolunteerLogin()

        when:
        webClient.post()
                .uri('/themes/register')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(themeDto)
                .retrieve()
                .toBodilessEntity()
                .block()

        then: "exception is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        themeRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as member, and create a Theme"() {
        given: 'member login'
        demoMemberLogin()

        when:
        webClient.post()
                .uri('/themes/register')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(themeDto)
                .retrieve()
                .toBodilessEntity()
                .block()

        then: "exception is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        themeRepository.count() == 0

        cleanup:
        deleteAll()
    }

}

