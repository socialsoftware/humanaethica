package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.auth

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.dto.AuthPasswordDto
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
        user = new Volunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        user.getAuthUser().setPassword(passwordEncoder.encode(SpockTest.USER_1_PASSWORD))
        userRepository.save(user)

        when:
        def result = webClient.post()
                .uri('/auth/user')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new AuthPasswordDto(SpockTest.USER_1_USERNAME, SpockTest.USER_1_PASSWORD))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check response status"
        result.token != ""
        result.user.username == SpockTest.USER_1_USERNAME
    }

    def cleanup() {
        deleteAll()
    }
}
