package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.auth

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.AuthPasswordDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Volunteer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetUserAuthWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    User user

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
    }

    def "user makes a login"() {
        given: "one inactive user with an expired "
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.SUBMITTED)
        user = userRepository.save(user)
        def authUser = AuthUser.createAuthUser(user.getId(), USER_1_USERNAME, USER_1_EMAIL, Type.NORMAL, Role.VOLUNTEER, USER_1_NAME)
        authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        authUserRepository.save(authUser)

        when:
        def result = webClient.post()
                .uri('/auth/user')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new AuthPasswordDto(USER_1_USERNAME, USER_1_PASSWORD))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check response status"
        result.token != ""
        result.user.username == USER_1_USERNAME
    }

    def cleanup() {
        deleteAll()
    }
}
