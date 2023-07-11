package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterThemeWebServiceIT extends SpockTest {
    public static final String THEME_1__NAME = "THEME_1_NAME"
    @LocalServerPort
    private int port

    def response

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)
    }

    def "login as admin, and create a Theme"() {
        given: 'admin login'
        demoAdminLogin()

        when:
        response = restClient.post(
                path: '/themes/register',
                body: [
                        name: THEME_1__NAME
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == HttpStatus.SC_OK
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
        response = restClient.post(
                path: '/themes/register',
                body: [
                        name: THEME_1__NAME
                ],
                requestContentType: 'application/json'
        )

        then: "exception is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        themeRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as member, and create a Theme"() {
        given: 'member login'
        demoMemberLogin()

        when:
        response = restClient.post(
                path: '/themes/register',
                body: [
                        name: THEME_1__NAME
                ],
                requestContentType: 'application/json'
        )

        then: "exception is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        themeRepository.count() == 0

        cleanup:
        deleteAll()
    }

}

