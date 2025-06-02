package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.user

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterUserWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def admin = new Admin(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,  User.State.SUBMITTED)
        admin = userRepository.save(admin)
        def authUser = AuthUser.createAuthUser(admin.getId(), USER_2_USERNAME, USER_2_EMAIL, Type.DEMO, Role.ADMIN, USER_2_NAME)
        authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        authUserRepository.save(authUser)
        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }

    def "login as admin, and create a user"() {
        given:
        def registerUserDto = new RegisterUserDto()
        registerUserDto.setUsername(USER_1_USERNAME)
        registerUserDto.setEmail(USER_1_EMAIL)
        registerUserDto.setRole(Role.VOLUNTEER)

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
        response.role == Role.VOLUNTEER.name()

        cleanup:
        deleteAll()
    }

}
