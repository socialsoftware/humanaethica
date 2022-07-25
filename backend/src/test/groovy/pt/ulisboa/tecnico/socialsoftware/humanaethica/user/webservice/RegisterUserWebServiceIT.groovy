package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterUserWebServiceIT extends SpockTest {
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

    def "login as admin, and create a user"() {
        when:
        response = restClient.post(
                path: '/users/register/',
                body: [
                        admin   : false,
                        username: USER_1_USERNAME,
                        email   : USER_1_EMAIL,
                        role    : 'VOLUNTEER'
                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200
        response.data != null
        response.data.username == USER_1_USERNAME
        response.data.email == USER_1_EMAIL
        response.data.role == "VOLUNTEER"

        cleanup:
        deleteAll()
    }

}
