package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.user

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto.RegisterUserDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterUserWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def admin = new Admin(SpockTest.USER_2_NAME, SpockTest.USER_2_USERNAME, SpockTest.USER_2_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)
        admin.authUser.setPassword(passwordEncoder.encode(SpockTest.USER_2_PASSWORD))
        userRepository.save(admin)

        normalUserLogin(SpockTest.USER_2_USERNAME, SpockTest.USER_2_PASSWORD)
    }

    def "login as admin, and create a user"() {
        given:
        def registerUserDto = new RegisterUserDto()
        registerUserDto.setUsername(SpockTest.USER_1_USERNAME)
        registerUserDto.setEmail(SpockTest.USER_1_EMAIL)
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
        response.username == SpockTest.USER_1_USERNAME
        response.email == SpockTest.USER_1_EMAIL
        response.role == User.Role.VOLUNTEER.name()

        cleanup:
        deleteAll()
    }

}
