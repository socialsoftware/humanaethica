package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterUserWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def admin = new Admin(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)
        admin.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(admin)

        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }

    def "login as admin, and create a user"() {
        given:
        def registerUserDto = new RegisterUserDto()
        registerUserDto.setUsername(USER_1_USERNAME)
        registerUserDto.setEmail(USER_1_EMAIL)
        registerUserDto.setRole(User.Role.VOLUNTEER)

        when:
        def response = webClient.post()
                .uri('/users/register')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(registerUserDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block()

        then: "check response status"
        response.username == USER_1_USERNAME
        response.email == USER_1_EMAIL
        response.role == User.Role.VOLUNTEER.name()

        cleanup:
        deleteAll()
    }

}
