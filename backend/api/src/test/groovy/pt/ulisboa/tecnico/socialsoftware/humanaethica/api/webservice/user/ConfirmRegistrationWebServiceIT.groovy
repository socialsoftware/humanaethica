package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.user

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfirmRegistrationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def registerUserDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        registerUserDto = new RegisterUserDto()
        registerUserDto.setUsername(USER_1_USERNAME)
        registerUserDto.setPassword(USER_1_PASSWORD)
        registerUserDto.setConfirmationToken(USER_1_TOKEN)
    }

    def "user confirms registration"() {
        given: "one inactive user"
        def user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.SUBMITTED)
        user = userRepository.save(user)
        def authuser = AuthUser.createAuthUser(user.getId(), USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER)
        authuser.setConfirmationToken(USER_1_TOKEN)
        authuser.setTokenGenerationDate(NOW)
        authUserRepository.save(authuser)


        when:
        def response = webClient.post()
                .uri('/users/register/confirm')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(registerUserDto)
                .retrieve()
                .bodyToMono(RegisterUserDto.class)
                .block()

        then: "check response status"
        response.email == USER_1_EMAIL
        response.username == USER_1_USERNAME
        response.active
        response.role == Role.VOLUNTEER
    }

    def "user tries to confirm registration with an expired token"() {
        given: "one inactive user with an expired token"
        def user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.SUBMITTED)
        user = userRepository.save(user)
        def authuser = AuthUser.createAuthUser(user.getId(), USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER)
        authuser.setConfirmationToken(USER_1_TOKEN)
        authuser.setTokenGenerationDate(TWO_DAYS_AGO)
        authUserRepository.save(authuser)

        when:
        def response = webClient.post()
                .uri('/users/register/confirm')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(registerUserDto)
                .retrieve()
                .bodyToMono(RegisterUserDto.class)
                .block()


        then: "check response status"
        response.email == USER_1_EMAIL
        response.username == USER_1_USERNAME
        response.active == false
        response.role == Role.VOLUNTEER
    }

    def cleanup() {
        deleteAll()
    }
}
