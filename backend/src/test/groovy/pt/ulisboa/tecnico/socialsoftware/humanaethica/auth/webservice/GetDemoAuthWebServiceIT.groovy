package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDemoAuthWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
    }

    def "demo volunteer login"() {
        when:
        def response = webClient.get()
                .uri('/auth/demo/volunteer')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check response "
        response.user.name == DemoUtils.DEMO_VOLUNTEER
        response.user.role == User.Role.VOLUNTEER
    }

    def "demo member login"() {
        when:
        def response = webClient.get()
                .uri('/auth/demo/member')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AuthDto.class)
                .block()

        then: "check response status"
        response.user.name == DemoUtils.DEMO_MEMBER
        response.user.role == User.Role.MEMBER
    }

    def cleanup() {
        deleteAll()
    }

}
