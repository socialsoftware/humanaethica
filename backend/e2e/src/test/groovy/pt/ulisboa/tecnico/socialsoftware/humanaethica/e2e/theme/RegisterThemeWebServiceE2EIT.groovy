package pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e.theme

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification


import static io.restassured.RestAssured.given

@Testcontainers
class RegisterThemeWebServiceE2EIT extends Specification {

    public static final String THEME_1_NAME = "THEME_1"

    @Shared
    DockerComposeContainer<?> env =
            new DockerComposeContainer<>(new File("docker-compose.test.yml"))
                    .withLocalCompose(true)
                    .withBuild(false)
                    .withRemoveImages(DockerComposeContainer.RemoveImages.LOCAL)

    def setupSpec() {
        env.start()


        Thread.sleep(240_000)

        RestAssured.baseURI = "http://localhost:18080"
    }

    def cleanupSpec() {
        env.stop()
    }


    private static String demoLogin(String role) {
        def resp = given()
                .accept(ContentType.JSON)
                .when()
                .get("/auth/demo/${role}")
                .then()
                .statusCode(200)
                .extract()
                .response()


        def token = resp.jsonPath().getString("token")
        if (!token) token = resp.jsonPath().getString("accessToken")
        if (!token) token = resp.getHeader("X-Auth-Token")
        assert token : "JWT não encontrado na resposta de /auth/demo/${role}"
        return token
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
        def token = demoLogin("admin")

        when:
        int status = registerTheme(token, THEME_1_NAME)

        then:
        assert status >= 200 && status < 300
    }

    def "login as volunteer, and create a Theme"() {
        given:
        def token = demoLogin("volunteer")

        when:
        int status = registerTheme(token, THEME_1_NAME)

        then:
        status == 403
    }

    def "login as member, and create a Theme"() {
        given:
        def token = demoLogin("member")

        when:
        int status = registerTheme(token, THEME_1_NAME)

        then:
        status == 403
    }
}
