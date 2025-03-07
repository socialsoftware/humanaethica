package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.user

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto.RegisterUserDto

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
        registerUserDto.setUsername(SpockTest.USER_1_USERNAME)
        registerUserDto.setPassword(SpockTest.USER_1_PASSWORD)
        registerUserDto.setConfirmationToken(SpockTest.USER_1_TOKEN)
    }

    def "user confirms registration"() {
        given: "one inactive user"
        def user = new Volunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        user.getAuthUser().setConfirmationToken(SpockTest.USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(SpockTest.NOW)
        userRepository.save(user)

        when:
        def response = webClient.post()
                .uri('/users/register/confirm')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(registerUserDto)
                .retrieve()
                .bodyToMono(RegisterUserDto.class)
                .block()

        then: "check response status"
        response.email == SpockTest.USER_1_EMAIL
        response.username == SpockTest.USER_1_USERNAME
        response.active
        response.role == User.Role.VOLUNTEER
    }

    def "user tries to confirm registration with an expired token"() {
        given: "one inactive user with an expired token"
        def user = new Volunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        user.getAuthUser().setConfirmationToken(SpockTest.USER_1_TOKEN)
        user.getAuthUser().setTokenGenerationDate(SpockTest.TWO_DAYS_AGO)
        userRepository.save(user)

        when:
        def response = webClient.post()
                .uri('/users/register/confirm')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(registerUserDto)
                .retrieve()
                .bodyToMono(RegisterUserDto.class)
                .block()


        then: "check response status"
        response.email == SpockTest.USER_1_EMAIL
        response.username == SpockTest.USER_1_USERNAME
        response.active == false
        response.role == User.Role.VOLUNTEER
    }

    def cleanup() {
        deleteAll()
    }
}
