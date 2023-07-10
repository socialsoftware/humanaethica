package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def activity
    def activityDto
    def institutionDto
    def institution
    def theme
    def member
    def admin

    def setup() {
        deleteAll()

        def post = new URL("https://jira/rest/api/latest/issue").openConnection();

        restClient = new RESTClient("http://localhost:" + port)

        institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)
        institution = institutionService.registerInstitution(institutionDto)

        member = new Member(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.DEMO, institution, User.State.SUBMITTED)
        member.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(member)

        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)
    }

    def "validate activity"() {
        given: "one activity"

        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED,null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,true,true))

        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setDescription("ACTIVITY_1_DESCRIPTION")
        activityDto.setStartingDate("2023-05-26T19:09:00Z");
        activityDto.setEndingDate("2023-05-26T22:09:00Z");
        activityDto.setInstitution(new InstitutionDto(institution))
        activityDto.setThemes(themes)
        activity = activityService.registerActivity(member.getId(), activityDto)
        activityService.reportActivity(activity.getId())

        when:
        admin = new Admin(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.DEMO, User.State.SUBMITTED)
        admin.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(admin)

        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        response = restClient.put(
                path: '/activity/' + activity.getId() + '/validate'
        )

        then: "check response status"
        response.status == 200
        activityRepository.findAll().size() == 1

    }
}