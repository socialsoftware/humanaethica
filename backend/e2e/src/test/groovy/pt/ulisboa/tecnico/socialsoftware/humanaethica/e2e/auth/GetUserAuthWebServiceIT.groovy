package pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e.auth

import io.restassured.RestAssured
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.client.MultipartBodyBuilder

import org.testcontainers.spock.Testcontainers
import pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e.E2EEnvironment
import reactor.core.publisher.Mono
import spock.lang.Shared
import spock.lang.Specification

import org.springframework.http.MediaType
import static io.restassured.RestAssured.given
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient


import java.sql.*



@Testcontainers
class GetUserAuthWebServiceIT extends Specification {






    @Shared
    WebClient webClient

    def setupSpec() {
        E2EEnvironment.startIfNeeded()

        RestAssured.baseURI = "http://localhost:18080"
        webClient = WebClient.create(RestAssured.baseURI)
    }


    def "volunteer registers, confirms and logs in successfully"() {

        when:
        registerVolunteer(E2EEnvironment.USER_1_NAME ,E2EEnvironment.USER_1_USERNAME, E2EEnvironment.USER_1_EMAIL)

        and:
        def confirmationToken = getConfirmationTokenFromDb(E2EEnvironment.USER_1_USERNAME)

        and:
        confirmRegistration(E2EEnvironment.USER_1_USERNAME, E2EEnvironment.USER_1_EMAIL, E2EEnvironment.USER_1_PASSWORD, confirmationToken)

        and:
        def result = loginUser(E2EEnvironment.USER_1_USERNAME, E2EEnvironment.USER_1_PASSWORD)

        then:
        result?.token
        result.user.username == E2EEnvironment.USER_1_USERNAME
    }

    // ----------------------- Helper methods ---------------------------------------

    private void registerVolunteer( String name ,String username, String email) {
        def volunteerDto = [
                name    : name,
                username: username,
                email   : email
        ]

        def fileBytes = "%PDF-1.4\n% dummy\n".getBytes()
        def fileResource = new ByteArrayResource(fileBytes) {
            @Override String getFilename() { "doc.pdf" }
        }

        def builder = new MultipartBodyBuilder()
        builder.part("volunteer", volunteerDto)
                .header("Content-Type", "application/json")
        builder.part("file", fileResource)
                .header("Content-Type", "application/pdf")

        webClient.post()
                .uri('/users/registerVolunteer')
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toBodilessEntity()
                .onErrorResume { Mono.empty() }
                .block()
    }

    private static String confirmRegistration(String username, String email, String password, String confirmationToken) {
        return given()
                .contentType("application/json")
                .body([
                        username: username,
                        email: email,
                        password: password,
                        confirmationToken: confirmationToken
                ])
                .when()
                .post("/auth/register/confirm")
                .then()
                .statusCode(200)
                .extract().as(Map).token
    }

    private static Map loginUser(String username, String password) {
        return given()
                .contentType("application/json")
                .body([
                        username: username,
                        password: password
                ])
                .when()
                .post("/auth/user")
                .then()
                .statusCode(200)
                .extract().as(Map)
    }



    private static void waitForPostgresReady() {
        def url  = "jdbc:postgresql://localhost:5435/authuserdb"
        def user = "humanaethica"
        def pass = "humanaethica"

        long deadline = System.currentTimeMillis() + 60_000
        Throwable last = null
        while (System.currentTimeMillis() < deadline) {
            try (def c = DriverManager.getConnection(url, user, pass);
                 def ps = c.prepareStatement("SELECT 1");
                 def rs = ps.executeQuery()) {
                return
            } catch (Throwable t) {
                last = t
                Thread.sleep(500)
            }
        }
        throw new AssertionError("Postgres não ficou pronto em 60s", last)
    }

    private static String getConfirmationTokenFromDb(String username) {
        waitForPostgresReady()
        def url  = "jdbc:postgresql://localhost:5435/authuserdb"
        def user = "humanaethica"
        def pass = "humanaethica"
        def sql  = """
        SELECT confirmation_token
        FROM auth_users
        WHERE username = ?
        ORDER BY id DESC
        LIMIT 1
        """

        try (def conn = DriverManager.getConnection(url, user, pass);
             def ps = conn.prepareStatement(sql)) {
            ps.setString(1, username)
            try (def rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1)
            }
        }
        throw new AssertionError("confirmation_token não encontrado para ${username}")
    }



}
