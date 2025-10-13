package pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e.theme

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification
import pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e.E2EEnvironment

import static io.restassured.RestAssured.given

@Testcontainers
class RegisterThemeWebServiceE2EIT extends Specification {




    def setupSpec() {
        E2EEnvironment.startIfNeeded()

        RestAssured.baseURI = "http://localhost:18080"
    }



    private static int registerTheme(String jwt, String themeName) {
        def payload = [name: themeName]
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer ${jwt}")
                .body(payload)
                .when()
                .post("/themes/register")
                .then()
                .extract()
                .statusCode()
    }


    def "login as admin, and create a Theme"() {
        given:
        def token = E2EEnvironment.demoLogin("admin")

        when:
        int status = registerTheme(token, E2EEnvironment.THEME_1_NAME)

        then:
        assert status >= 200 && status < 300
    }

    def "login as volunteer, and create a Theme"() {
        given:
        def token = E2EEnvironment.demoLogin("volunteer")

        when:
        int status = registerTheme(token, E2EEnvironment.THEME_1_NAME)

        then:
        status == 403
    }

    def "login as member, and create a Theme"() {
        given:
        def token = E2EEnvironment.demoLogin("member")

        when:
        int status = registerTheme(token, E2EEnvironment.THEME_1_NAME)

        then:
        status == 403
    }
}
