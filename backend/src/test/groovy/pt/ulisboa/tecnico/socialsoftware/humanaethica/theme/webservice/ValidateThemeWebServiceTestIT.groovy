package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateThemeWebServiceTestIT extends SpockTest {
    public static final String THEME__NAME_1 = "THEME_NAME_1"
    @LocalServerPort
    private int port

    def response
    def theme
    def result

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        theme = new ThemeDto()
        theme.setName(THEME__NAME_1)
        result = themeService.registerTheme(theme,false)
    }


    def "admin validate theme"() {
        given: 'admin login'
        demoAdminLogin()

        when:
        response = restClient.put(
                path: '/themes/' + result.getId() + '/validate'
        )

        then: "check response status"
        response.status == HttpStatus.SC_OK
        response.data != null
        and: "check if theme is active"
        themeRepository.findAll().size() == 1
        def theme = themeRepository.findAll().get(0)
        theme.isActive()
        theme.state == Theme.State.APPROVED
    }

    def "volunteer validate theme"() {
        given: 'volunteer login'
        demoVolunteerLogin()

        when:
        response = restClient.put(
                path: '/themes/' + result.getId() + '/validate'
        )

        then: "check response status"
        then: "exception is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
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
        response = restClient.put(
                path: '/themes/' + result.getId() + '/validate'
        )

        then: "check response status"
        then: "exception is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and: "theme is not active"
        themeRepository.findAll().size() == 1
        def theme = themeRepository.findAll().get(0)
        !theme.isActive()
        theme.state == Theme.State.SUBMITTED
    }}


