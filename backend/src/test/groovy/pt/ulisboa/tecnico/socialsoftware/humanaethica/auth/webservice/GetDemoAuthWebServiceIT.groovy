package pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDemoAuthWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)
    }

    def "demo volunteer login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/volunteer'
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DemoUtils.DEMO_VOLUNTEER
        response.data.user.role == User.Role.VOLUNTEER.toString()
    }

    def "demo member login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/member'
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DemoUtils.DEMO_MEMBER
        response.data.user.role == User.Role.MEMBER.toString()
    }

    def cleanup() {
        deleteAll()
    }

}
