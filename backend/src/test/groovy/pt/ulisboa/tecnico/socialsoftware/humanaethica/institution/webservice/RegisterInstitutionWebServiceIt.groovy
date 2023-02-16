package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterInstitutionWebserviceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def user

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)
    }

    def "create new institution"() {
        when:
        response = restClient.post(
                path: '/institution/register',
                body: [
                        institutionEmail: INSTITUTION_1_EMAIL,
                        institutionName : INSTITUTION_1_NAME,
                        institutionNif  : INSTITUTION_1_NIF,
                        memberUsername  : USER_1_USERNAME,
                        memberEmail     : USER_1_EMAIL,
                        memberName      : USER_1_NAME,

                ],
                requestContentType: 'application/json'
        )

        then: "the institution and member are saved in the database"
        response.status == 200
        response.data == null
        institutionRepository.findAll().size() == 1
        userRepository.findAll().size() == 1
        def institution = institutionRepository.findAll().get(0)
        def user = userRepository.findAll().get(0)
        institution.getConfirmationToken() != null
        and: "checks if institution/member data is correct"
        institution.getName() == INSTITUTION_1_NAME
        institution.getEmail() == INSTITUTION_1_EMAIL
        institution.getNIF() == INSTITUTION_1_NIF
        user.getName() == USER_1_NAME
        user.getEmail() == USER_1_EMAIL
        user.getUsername() == USER_1_USERNAME
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