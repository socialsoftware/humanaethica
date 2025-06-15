package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.auth

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.dto.AuthDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role

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
        response.user.username == DemoUtils.DEMO_VOLUNTEER.toLowerCase()
        response.user.role == Role.VOLUNTEER
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
        response.user.username == DemoUtils.DEMO_MEMBER.toLowerCase()
        response.user.role == Role.MEMBER
    }

    def cleanup() {
        deleteAll()
    }

}
