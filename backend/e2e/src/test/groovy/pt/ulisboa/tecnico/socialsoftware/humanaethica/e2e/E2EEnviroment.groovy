package pt.ulisboa.tecnico.socialsoftware.humanaethica.e2e

import io.restassured.http.ContentType
import org.testcontainers.containers.DockerComposeContainer

import static io.restassured.RestAssured.given

class E2EEnvironment {



    public static final String THEME_1_NAME = "THEME_1"
    public static final String USER_1_NAME = "User 1 Name"
    public static final String USER_1_USERNAME = "rfs"
    public static final String USER_1_EMAIL = "user1@mail.com"
    public static final String USER_1_PASSWORD = "1234@WS4544"


    private static DockerComposeContainer<?> compose
    private static boolean started = false

    static void startIfNeeded() {
        if (started) return

        compose = new DockerComposeContainer<>(new File("docker-compose.test.yml"))
                        .withLocalCompose(true)
                        .withBuild(false)
                        .withRemoveImages(DockerComposeContainer.RemoveImages.LOCAL)

        compose.start()

        Thread.sleep(240_000)
        started = true
    }

    static String demoLogin(String role) {
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

}