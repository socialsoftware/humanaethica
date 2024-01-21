package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityId

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        def user = demoMemberLogin()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        def themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,false,false,false))

        def activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumber(2)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setEndingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(NOW))
        activityDto.setThemes(themes)
        def activity = activityService.registerActivity(user.id, activityDto)

        activityId = activity.id
    }

    def "volunteer reports activity"() {
        given:
        demoVolunteerLogin()

        when:
        def response = restClient.put(
                path: '/activity/' + activityId + '/report'
        )

        then: "check response status"
        response.status == HttpStatus.SC_OK
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.REPORTED
    }

    def "volunteer reports activity with wrong id"() {
        given:
        demoVolunteerLogin()

        when:
        restClient.put(
                path: '/activity/' + '222' + '/report'
        )

        then: "error"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_BAD_REQUEST
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }

    def "member tries to report activity"() {
        given:
        demoMemberLogin()

        when:
        restClient.put(
                path: '/activity/' + activityId + '/report'
        )

        then: "error is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }

    def "admin tries to report activity"() {
        given: "login volunteer"
        demoAdminLogin()

        when:
        restClient.put(
                path: '/activity/' + activityId + '/report'
        )

        then: "error is thrown"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.findAll().size() == 1
        def activity = activityRepository.findAll().get(0)
        activity.state == Activity.State.APPROVED
    }
}