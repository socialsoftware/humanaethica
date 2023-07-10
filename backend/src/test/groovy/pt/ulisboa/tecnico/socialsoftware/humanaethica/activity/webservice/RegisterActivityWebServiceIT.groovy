package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def theme
    def institutionDto
    def institution

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)
        institution = institutionService.registerInstitution(institutionDto)

        def member = new Member(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.DEMO, institution, User.State.SUBMITTED)
        member.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(member)

        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }

    def "login as member, and create an activity"() {
        given:
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,true,true))

        when:
        response = restClient.post(
                path: '/activity/memberRegister',
                body: [
                        name         :"ACTIVITY_1_NAME",
                        region       :"ACTIVITY_1_REGION",
                        themes       :themes,
                        description  :"ACTIVITY_1_DESCRIPTION",
                        startingDate :"2023-07-03T12:20:00Z",
                        endingDate   :"2023-07-07T12:20:00Z"

                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == 200
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == "ACTIVITY_1_NAME"
        activity.getRegion() == "ACTIVITY_1_REGION"
        activity.getDescription() == "ACTIVITY_1_DESCRIPTION"

        /*cleanup:
        deleteAll()*/
    }

}
