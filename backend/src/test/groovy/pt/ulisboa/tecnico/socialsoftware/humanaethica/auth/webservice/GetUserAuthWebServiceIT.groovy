package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.webservice

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthPasswordDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetUserAuthWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    User user

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)
    }

    def "user makes a login"() {
        given: "one inactive user with an expired "
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        user.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(user)

        when:
        def response = restClient.post(
                path: '/auth/user',
                body: JsonOutput.toJson(new AuthPasswordDto(USER_1_USERNAME, USER_1_PASSWORD)),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.username == USER_1_USERNAME
    }

    def cleanup() {
        deleteAll()
    }
}
