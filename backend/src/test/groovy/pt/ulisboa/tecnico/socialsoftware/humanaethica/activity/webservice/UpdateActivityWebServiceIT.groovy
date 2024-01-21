package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def themes
    def activityId

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)

        def user = demoMemberLogin()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        themes = new ArrayList<>()
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

    def "login as member, and update an activity"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member registers the activity'
        def response = restClient.put(
                path: '/activity/' + activityId + '/update',
                body: [
                        name         : ACTIVITY_NAME_2,
                        region       : ACTIVITY_REGION_2,
                        participantsNumber: 4,
                        themes       : themes,
                        description  : ACTIVITY_DESCRIPTION_2,
                        startingDate : DateHandler.toISOString(IN_TWO_DAYS),
                        endingDate   : DateHandler.toISOString(IN_THREE_DAYS),
                        applicationDeadline   : DateHandler.toISOString(IN_ONE_DAY)

                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        response != null
        response.status == HttpStatus.SC_OK
        and: 'data'
        response.data.name == ACTIVITY_NAME_2
        response.data.region == ACTIVITY_REGION_2
        response.data.participantsNumber == 4
        response.data.description == ACTIVITY_DESCRIPTION_2
        response.data.startingDate == DateHandler.toISOString(IN_TWO_DAYS)
        response.data.endingDate== DateHandler.toISOString(IN_THREE_DAYS)
        response.data.applicationDeadline == DateHandler.toISOString(IN_ONE_DAY)
        response.data.themes[0].name == THEME_NAME_1
        and: 'check database'
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == ACTIVITY_NAME_2
        activity.getRegion() == ACTIVITY_REGION_2
        activity.getParticipantsNumber() == 4
        activity.getDescription() == ACTIVITY_DESCRIPTION_2
        activity.getStartingDate().withNano(0) == IN_TWO_DAYS.withNano(0)
        activity.getEndingDate().withNano(0) == IN_THREE_DAYS.withNano(0)
        activity.getApplicationDeadline().withNano(0) == IN_ONE_DAY.withNano(0)
        activity.themes.get(0).getName() == THEME_NAME_1

        cleanup:
        deleteAll()
    }

    def "update with empty name abort and no changes"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member registers the activity'
        restClient.put(
                path: '/activity/' + activityId + '/update',
                body: [
                        name         : '   ',
                        region       : ACTIVITY_REGION_2,
                        participantsNumber: 4,
                        themes       : themes,
                        description  : ACTIVITY_DESCRIPTION_2,
                        startingDate : DateHandler.toISOString(IN_TWO_DAYS),
                        endingDate   : DateHandler.toISOString(IN_THREE_DAYS),
                        applicationDeadline   : DateHandler.toISOString(IN_ONE_DAY)

                ],
                requestContentType: 'application/json'
        )

        then: "check response status"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_BAD_REQUEST
        and: 'check database'
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == ACTIVITY_NAME_1
        activity.getRegion() == ACTIVITY_REGION_1
        activity.getParticipantsNumber() == 2
        activity.getDescription() == ACTIVITY_DESCRIPTION_1
        activity.getStartingDate().withNano(0) == IN_ONE_DAY.withNano(0)
        activity.getEndingDate().withNano(0) == IN_TWO_DAYS.withNano(0)
        activity.getApplicationDeadline().withNano(0) == NOW.withNano(0)
        activity.themes.get(0).getName() == THEME_NAME_1

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and update an activity"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'the volunteer registers the activity'
        restClient.put(
                path: '/activity/' + activityId + '/update',
                body: [
                        name         : ACTIVITY_NAME_2,
                        region       : ACTIVITY_REGION_2,
                        participantsNumber: 4,
                        themes       : themes,
                        description  : ACTIVITY_DESCRIPTION_2,
                        startingDate : DateHandler.toISOString(IN_TWO_DAYS),
                        endingDate   : DateHandler.toISOString(IN_THREE_DAYS),
                        applicationDeadline   : DateHandler.toISOString(IN_ONE_DAY)

                ],
                requestContentType: 'application/json' )

        then: "an error is returned"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def "login as admin, and update an activity"() {
        given: 'a demo'
        demoAdminLogin()

        when: 'the admin registers the activity'
        restClient.put(
                path: '/activity/' + activityId + '/update',
                body: [
                        name         : ACTIVITY_NAME_2,
                        region       : ACTIVITY_REGION_2,
                        participantsNumber: 4,
                        themes       : themes,
                        description  : ACTIVITY_DESCRIPTION_2,
                        startingDate : DateHandler.toISOString(IN_TWO_DAYS),
                        endingDate   : DateHandler.toISOString(IN_THREE_DAYS),
                        applicationDeadline   : DateHandler.toISOString(IN_ONE_DAY)

                ],
                requestContentType: 'application/json' )

        then: "an error is returned"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        activityRepository.count() == 1

        cleanup:
        deleteAll()
    }

}
