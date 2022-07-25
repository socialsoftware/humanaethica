package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin


import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateInstitutionWebserviceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def institution

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        def admin = new Admin(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)
        admin.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(admin)

        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }
    
    def "validate institution"() {
        given: "one institution"
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)

        when:
        response = restClient.post(
                path: '/institution/' + institution.getId() + '/validate'
        )

        then: "check response status"
        response.status == 200
        response.data == null
        and: "check if institution is active"
        institutionRepository.findAll().size() == 1
        userRepository.findAll().size() == 1
        def institution = institutionRepository.findAll().get(0)
        institution.isActive()
    }
}