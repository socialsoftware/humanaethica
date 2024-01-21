package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def activity
    def activityDto
    def institution
    def theme

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        def user = demoMemberLogin()

        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        List<ThemeDto> themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,false,false,false))

        activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setParticipantsNumber(2)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setStartingDate(DateHandler.toISOString(NOW))
        activityDto.setEndingDate(DateHandler.toISOString(IN_ONE_DAY))
        activityDto.setApplicationDeadline(DateHandler.toISOString(TWO_DAYS_AGO))
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