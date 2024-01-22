package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
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
    def activityDto2

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        themes = new ArrayList<>()
        themes.add(new ThemeDto(theme,false,false,false))

        def activityDto1 = new ActivityDto()
        activityDto1.setName(ACTIVITY_NAME_1)
        activityDto1.setRegion(ACTIVITY_REGION_1)
        activityDto1.setParticipantsNumber(2)
        activityDto1.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto1.setStartingDate(DateHandler.toISOString(IN_ONE_DAY))
        activityDto1.setEndingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto1.setApplicationDeadline(DateHandler.toISOString(NOW))
        activityDto1.setThemes(themes)
        def activity = activityService.registerActivity(user.id, activityDto1)

        activityId = activity.id

        activityDto2 = new ActivityDto()
        activityDto2.setName(ACTIVITY_NAME_2)
        activityDto2.setRegion(ACTIVITY_REGION_2)
        activityDto2.setParticipantsNumber(4)
        activityDto2.setDescription(ACTIVITY_DESCRIPTION_2)
        activityDto2.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto2.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto2.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))
        activityDto2.setThemes(new ArrayList<>())
    }

    def "login as member, and update an activity"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member registers the activity'
        def response = webClient.put()
                .uri('/activity/' + activityId + '/update')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto2)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response"
        response.name == ACTIVITY_NAME_2
        response.region == ACTIVITY_REGION_2
        response.participantsNumber == 4
        response.description == ACTIVITY_DESCRIPTION_2
        response.startingDate == DateHandler.toISOString(IN_TWO_DAYS)
        response.endingDate== DateHandler.toISOString(IN_THREE_DAYS)
        response.applicationDeadline == DateHandler.toISOString(IN_ONE_DAY)
        response.themes.isEmpty()
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
        activity.themes.isEmpty()

        cleanup:
        deleteAll()
    }

    def "update with 10 participants abort and no changes"() {
        given: 'a member'
        demoMemberLogin()
        and:
        activityDto2.setParticipantsNumber(10)

        when: 'the member registers the activity'
        webClient.put()
                .uri('/activity/' + activityId + '/update')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto2)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
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
        webClient.put()
                .uri('/activity/' + activityId + '/update')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto2)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def "login as admin, and update an activity"() {
        given: 'a demo'
        demoAdminLogin()

        when: 'the admin registers the activity'
        webClient.put()
                .uri('/activity/' + activityId + '/update')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto2)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.count() == 1

        cleanup:
        deleteAll()
    }

}
