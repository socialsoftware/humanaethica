package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfirmRegistrationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def user

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)
    }

    def "user confirms registration"() {
        given: "one inactive user"
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_TODAY)
        userRepository.save(user)

        when:
        response = restClient.post(
                path: '/users/register/confirm',
                body: [
                        username         : USER_1_USERNAME,
                        password         : USER_1_PASSWORD,
                        confirmationToken: USER_1_TOKEN
                ],
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data != null
        response.data.email == USER_1_EMAIL
        response.data.username == USER_1_USERNAME
        response.data.active == true
        response.data.role == "VOLUNTEER"
    }

    def "user tries to confirm registration with an expired token"() {
        given: "one inactive user with an expired token"
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        user.getAuthUser().setConfirmationToken(USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(LOCAL_DATE_BEFORE)
        userRepository.save(user)

        when:
        response = restClient.post(
                path: '/users/register/confirm',
                body: [
                        username         : USER_1_USERNAME,
                        password         : USER_1_PASSWORD,
                        confirmationToken: USER_1_TOKEN
                ],
                requestContentType: 'application/json'
        )


        then: "check response status"
        response.status == 200
        response.data != null
        response.data.email == USER_1_EMAIL
        response.data.username == USER_1_USERNAME
        response.data.active == false
        response.data.role == "VOLUNTEER"
    }

    def cleanup() {
        deleteAll()
    }
}
