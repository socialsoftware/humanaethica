package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterThemeWebServiceIt extends SpockTest {
    @LocalServerPort
    private int port

    def response

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        def admin = new Admin(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)
        admin.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(admin)

        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }

    def "login as admin, and create a Theme"() {
        when:
        response = restClient.post(
                path: '/themes/register',
                body: [
                        name: "THEME_1_NAME"
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200
        themeRepository.count() == 1

        cleanup:
        deleteAll()
    }

}

