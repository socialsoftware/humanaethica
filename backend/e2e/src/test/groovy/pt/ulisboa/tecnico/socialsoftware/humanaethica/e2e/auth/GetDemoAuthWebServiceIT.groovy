package pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e.auth

import io.restassured.RestAssured
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.spock.Testcontainers
import pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e.E2EEnvironment
import spock.lang.Shared
import spock.lang.Specification


import static io.restassured.RestAssured.given

@Testcontainers
class GetDemoAuthWebServiceIT extends Specification {


    def setupSpec() {

        E2EEnvironment.startIfNeeded()


        RestAssured.baseURI = "http://localhost:18080"
    }


    def "demo volunteer login via Gateway"() {
        when:
        Map resp = given()
                .when().get("/auth/demo/volunteer")
                .then().statusCode(200)
                .extract().as(Map)

        then:
        def user = resp.user as Map
        assert user.username?.toString()?.toLowerCase() == "demo-volunteer"
        assert user.role in ["VOLUNTEER", "ROLE_VOLUNTEER"]
    }

    def "demo member login via Gateway"() {
        when:
        Map resp = given()
                .when().get("/auth/demo/member")
                .then().statusCode(200)
                .extract().as(Map)

        then:
        def user = resp.user as Map
        assert user.username?.toString()?.toLowerCase() == "demo-member"
        assert user.role in ["MEMBER", "ROLE_MEMBER"]
    }
}