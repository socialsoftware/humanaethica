package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.institution

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.dto.RegisterInstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import spock.lang.Ignore

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterInstitutionWebserviceIT extends SpockTest {
    @LocalServerPort
    private int port

    def registerInstitutionDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        registerInstitutionDto = new RegisterInstitutionDto()
        registerInstitutionDto.setInstitutionEmail(SpockTest.INSTITUTION_1_EMAIL)
        registerInstitutionDto.setInstitutionName(SpockTest.INSTITUTION_1_NAME)
        registerInstitutionDto.setInstitutionNif(SpockTest.INSTITUTION_1_NIF)
        registerInstitutionDto.setMemberEmail(SpockTest.USER_1_EMAIL)
        registerInstitutionDto.setMemberUsername(SpockTest.USER_1_USERNAME)
        registerInstitutionDto.setMemberName(SpockTest.USER_1_NAME)
    }

    @Ignore
    def "create new institution"() {
        when:
        def response = webClient.post()
                .uri('/activity/register')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(registerInstitutionDto)
                .retrieve()
                .bodyToMono(RegisterInstitutionDto.class)
                .block()

        then: "the institution and member are saved in the database"
        institutionRepository.findAll().size() == 1
        userRepository.findAll().size() == 1
        def institution = institutionRepository.findAll().get(0)
        def user = userRepository.findAll().get(0)
        institution.getConfirmationToken() != null
        and: "checks if institution/member data is correct"
        institution.getName() == SpockTest.INSTITUTION_1_NAME
        institution.getEmail() == SpockTest.INSTITUTION_1_EMAIL
        institution.getNIF() == SpockTest.INSTITUTION_1_NIF
        user.getName() == SpockTest.USER_1_NAME
        user.getEmail() == SpockTest.USER_1_EMAIL
        user.getUsername() == SpockTest.USER_1_USERNAME
        user.getRole() == User.Role.MEMBER
        user.getInstitution().getId() == institution.getId()
        institution.getMembers().size() == 1
        !institution.isActive()
        def authUser = (AuthNormalUser) authUserRepository.findAll().get(0)
        !authUser.isActive()
    }

    def cleanup() {
        deleteAll()
    }
}