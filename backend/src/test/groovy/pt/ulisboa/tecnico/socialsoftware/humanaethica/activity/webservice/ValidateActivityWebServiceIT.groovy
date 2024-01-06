package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
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
    public static final String THEME_1__NAME = "THEME_1_NAME"
    public static final String ACTIVITY_1__NAME = "ACTIVITY_1_NAME"
    public static final String ACTIVITY_1__REGION = "ACTIVITY_1_REGION"
    public static final String ACTIVITY_1__DESCRIPTION = "ACTIVITY_1_DESCRIPTION"
    public static final String STARTING_DATE = "2023-05-26T19:09:00Z"
    public static final String ENDING_DATE = "2023-05-26T22:09:00Z"
    @LocalServerPort
    private int port

    def response
    def activity
    def activityDto
    def institution
    def theme
    def member
    def admin

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        def user = demoMemberLogin()

        theme = new Theme(THEME_1__NAME, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,false,false,false))

        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_1__NAME)
        activityDto.setRegion(ACTIVITY_1__REGION)
        activityDto.setDescription(ACTIVITY_1__DESCRIPTION)
        activityDto.setStartingDate(STARTING_DATE);
        activityDto.setEndingDate(ENDING_DATE);
        activityDto.setInstitution(new InstitutionDto(institutionService.getDemoInstitution()))
        activityDto.setThemes(themes)
        activity = activityService.registerActivity(user.id, activityDto)
        activityService.reportActivity(activity.getId())
    }

    def "admin validate activity"() {
        given: "login admin"
        demoAdminLogin()

        when: 'validate'
        response = restClient.put(
                path: '/activity/' + activity.getId() + '/validate'
        )

        then: "check response status"
        response.status == HttpStatus.SC_OK
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }

    def "member validate activity"() {
        when: 'validate'
        response = restClient.put(
                path: '/activity/' + activity.getId() + '/validate'
        )

        then: "error is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.REPORTED
    }

    def "volunteer validate activity"() {
        given: "login 'volunteer"
        demoVolunteerLogin()

        when: 'validate'
        response = restClient.put(
                path: '/activity/' + activity.getId() + '/validate'
        )

        then: "error is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.REPORTED
    }
}