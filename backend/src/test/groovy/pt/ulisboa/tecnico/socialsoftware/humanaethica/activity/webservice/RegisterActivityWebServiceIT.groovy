package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.checkerframework.checker.units.qual.N
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response

    def theme
    def themes

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,false, false, false))
    }

    def "login as member, and create an activity"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member registers the activity'
        response = restClient.post(
                path: '/activity/register',
                body: [
                        name         : ACTIVITY_NAME_1,
                        region       : ACTIVITY_REGION_1,
                        participantsNumber: 2,
                        themes       : themes,
                        description  : ACTIVITY_DESCRIPTION_1,
                        startingDate : DateHandler.toISOString(NOW),
                        endingDate   : DateHandler.toISOString(IN_ONE_DAY),
                        applicationDeadline   : DateHandler.toISOString(ONE_DAY_AGO)

                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == HttpStatus.SC_OK
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == ACTIVITY_NAME_1
        activity.getRegion() == ACTIVITY_REGION_1
        activity.getParticipantsNumber() == 2
        activity.getDescription() == ACTIVITY_DESCRIPTION_1
        activity.getStartingDate().withNano(0) == NOW.withNano(0)
        activity.getEndingDate().withNano(0) == IN_ONE_DAY.withNano(0)
        activity.getApplicationDeadline().withNano(0) == ONE_DAY_AGO.withNano(0)
        activity.themes.get(0).getName() == THEME_NAME_1

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an activity"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'the volunteer registers the activity'
        response = restClient.post(
                path: '/activity/register',
                body: [
                        name         : ACTIVITY_NAME_1,
                        region       : ACTIVITY_REGION_1,
                        themes       : themes,
                        description  : ACTIVITY_DESCRIPTION_1,
                        startingDate : DateHandler.toISOString(NOW),
                        endingDate   : DateHandler.toISOString(IN_ONE_DAY)

                ],
                requestContentType: 'application/json'
        )

        then: "an error is returned"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create an activity"() {
        given: 'a demo'
        demoAdminLogin()

        when: 'the admin registers the activity'
        response = restClient.post(
                path: '/activity/register',
                body: [
                        name         : ACTIVITY_NAME_1,
                        region       : ACTIVITY_REGION_1,
                        themes       : themes,
                        description  : ACTIVITY_DESCRIPTION_1,
                        startingDate : DateHandler.toISOString(NOW),
                        endingDate   : DateHandler.toISOString(IN_ONE_DAY)

                ],
                requestContentType: 'application/json'
        )

        then: "an error is returned"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.count() == 0

        cleanup:
        deleteAll()
    }

}
