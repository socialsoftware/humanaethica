package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterActivityWebServiceIT extends SpockTest {
    public static final String THEME_1__NAME = "THEME_1_NAME"
    public static final String ACTIVITY_1__NAME = "ACTIVITY_1_NAME"
    public static final String ACTIVITY_1__REGION = "ACTIVITY_1_REGION"
    public static final String ACTIVITY_1__DESCRIPTION = "ACTIVITY_1_DESCRIPTION"
    public static final LocalDateTime STARTING_DATE = DateHandler.now().plusDays(1)
    public static final LocalDateTime ENDING_DATE = DateHandler.now().plusDays(2)
        public static final LocalDateTime APPLICATION_DEADLINE = DateHandler.now().plusDays(2)
    @LocalServerPort
    private int port

    def response

    def theme
    def themes

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        theme = new Theme(THEME_1__NAME, Theme.State.APPROVED,null)
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
                        name         : ACTIVITY_1__NAME,
                        region       : ACTIVITY_1__REGION,
                        themes       : themes,
                        description  : ACTIVITY_1__DESCRIPTION,
                        startingDate : DateHandler.toISOString(STARTING_DATE),
                        endingDate   : DateHandler.toISOString(ENDING_DATE),
                        applicationDeadline   : DateHandler.toISOString(APPLICATION_DEADLINE)

                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == HttpStatus.SC_OK
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == ACTIVITY_1__NAME
        activity.getRegion() == ACTIVITY_1__REGION
        activity.getDescription() == ACTIVITY_1__DESCRIPTION
        activity.getStartingDate() == STARTING_DATE
        activity.getEndingDate() == ENDING_DATE
        activity.themes.get(0).getName() == THEME_1__NAME

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
                        name         : ACTIVITY_1__NAME,
                        region       : ACTIVITY_1__REGION,
                        themes       : themes,
                        description  : ACTIVITY_1__DESCRIPTION,
                        startingDate : DateHandler.toISOString(STARTING_DATE),
                        endingDate   : DateHandler.toISOString(ENDING_DATE)

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
                        name         : ACTIVITY_1__NAME,
                        region       : ACTIVITY_1__REGION,
                        themes       : themes,
                        description  : ACTIVITY_1__DESCRIPTION,
                        startingDate : DateHandler.toISOString(STARTING_DATE),
                        endingDate   : DateHandler.toISOString(ENDING_DATE)

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
