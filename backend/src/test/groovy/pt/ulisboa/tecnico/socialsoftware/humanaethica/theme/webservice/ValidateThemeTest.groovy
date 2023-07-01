package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateThemeTest extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def theme
    def result

    def setup() {
        deleteAll()

        def post = new URL("https://jira/rest/api/latest/issue").openConnection();

        restClient = new RESTClient("http://localhost:" + port)

        def admin = new Admin(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)
        admin.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(admin)

        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }


    def "validate theme"() {
        given:

        theme = new ThemeDto()
        theme.setName("THEME_NAME_1")
        result = themeService.registerTheme(theme,false)

        when:
        System.out.println("AQUI       ")
        System.out.println(result.getId())

        response = restClient.put(
                path: '/themes/' + result.getId() + '/validate'
        )

        then: "check response status"
        response.status == 200
        response.data != null
        and: "check if theme is active"
        themeRepository.findAll().size() == 1
        def theme = themeRepository.findAll().get(0)
        theme.isActive()
    }


}


