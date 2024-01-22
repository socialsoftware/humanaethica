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
class ValidateThemeWebServiceTestIT extends SpockTest {
    public static final String THEME__NAME_1 = "THEME_NAME_1"
    @LocalServerPort
    private int port

    def theme
    def result

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        theme = new ThemeDto()
        theme.setName(THEME__NAME_1)
        result = themeService.registerTheme(theme,false)
    }

    def "admin validate theme"() {
        given: 'admin login'
        demoAdminLogin()

        when:
        webClient.put()
                .uri('/themes/' + result.getId() + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ThemeDto.class)
                .collectList()
                .block()

        then: "check if theme is active"
        themeRepository.findAll().size() == 1
        def theme = themeRepository.findAll().get(0)
        theme.isActive()
        theme.state == Theme.State.APPROVED
    }

    def "volunteer validate theme"() {
        given: 'volunteer login'
        demoVolunteerLogin()

        when:
        webClient.put()
                .uri('/themes/' + result.getId() + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ThemeDto.class)
                .collectList()
                .block()

        then: "exception is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "theme is not active"
        themeRepository.findAll().size() == 1
        def theme = themeRepository.findAll().get(0)
        !theme.isActive()
        theme.state == Theme.State.SUBMITTED
    }

    def "member validate theme"() {
        given: 'member login'
        demoMemberLogin()

        when:
        webClient.put()
                .uri('/themes/' + result.getId() + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ThemeDto.class)
                .collectList()
                .block()

        then: "exception is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "theme is not active"
        themeRepository.findAll().size() == 1
        def theme = themeRepository.findAll().get(0)
        !theme.isActive()
        theme.state == Theme.State.SUBMITTED
    }}


