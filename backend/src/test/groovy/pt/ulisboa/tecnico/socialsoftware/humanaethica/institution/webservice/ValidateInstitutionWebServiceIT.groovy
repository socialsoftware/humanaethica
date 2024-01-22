package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateInstitutionWebserviceIT extends SpockTest {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def admin = new Admin(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)
        admin.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(admin)

        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }

    def "validate institution"() {
        given: "one institution"
        def institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)
        def institution = institutionService.registerInstitution(institutionDto)

        when:
        def response = webClient.post()
                .uri('/institution/' + institution.getId() + '/validate')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(InstitutionDto.class)
                .collectList()
                .block()

        then: "check if institution is active"
        institutionRepository.findAll().size() == 1
        userRepository.findAll().size() == 1
        def storedInstitution = institutionRepository.findAll().get(0)
        storedInstitution.isActive()
    }
}