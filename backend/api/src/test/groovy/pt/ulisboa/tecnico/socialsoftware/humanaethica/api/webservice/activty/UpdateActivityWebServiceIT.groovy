package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.activty

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityDto
    def activityId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()

        def theme = createTheme(SpockTest.THEME_NAME_1, Theme.State.APPROVED,null)
        def themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))

        activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,2, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,themesDto)

        def activity = activityService.registerActivity(user.id, activityDto)

        activityId = activity.id

        activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_2, SpockTest.ACTIVITY_REGION_2,4, SpockTest.ACTIVITY_DESCRIPTION_2,
                SpockTest.NOW, SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS,new ArrayList<>())
    }

    def "login as member, and update an activity"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member registers the activity'
        def response = webClient.put()
                .uri('/activities/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response"
        response.name == SpockTest.ACTIVITY_NAME_2
        response.region == SpockTest.ACTIVITY_REGION_2
        response.participantsNumberLimit == 4
        response.description == SpockTest.ACTIVITY_DESCRIPTION_2
        response.startingDate == DateHandler.toISOString(SpockTest.IN_ONE_DAY)
        response.endingDate== DateHandler.toISOString(SpockTest.IN_TWO_DAYS)
        response.applicationDeadline == DateHandler.toISOString(SpockTest.NOW)
        response.themes.isEmpty()
        and: 'check database'
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == SpockTest.ACTIVITY_NAME_2
        activity.getRegion() == SpockTest.ACTIVITY_REGION_2
        activity.getParticipantsNumberLimit() == 4
        activity.getDescription() == SpockTest.ACTIVITY_DESCRIPTION_2
        activity.getStartingDate().withNano(0) == SpockTest.IN_ONE_DAY.withNano(0)
        activity.getEndingDate().withNano(0) == SpockTest.IN_TWO_DAYS.withNano(0)
        activity.getApplicationDeadline().withNano(0) == SpockTest.NOW.withNano(0)
        activity.themes.isEmpty()

        cleanup:
        deleteAll()
    }

    def "update with 10 participants abort and no changes"() {
        given: 'a member'
        demoMemberLogin()
        and:
        activityDto.setParticipantsNumberLimit(10)

        when: 'the member registers the activity'
        webClient.put()
                .uri('/activities/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        and: 'check database'
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == SpockTest.ACTIVITY_NAME_1
        activity.getRegion() == SpockTest.ACTIVITY_REGION_1
        activity.getParticipantsNumberLimit() == 2
        activity.getDescription() == SpockTest.ACTIVITY_DESCRIPTION_1
        activity.getStartingDate().withNano(0) == SpockTest.IN_TWO_DAYS.withNano(0)
        activity.getEndingDate().withNano(0) == SpockTest.IN_THREE_DAYS.withNano(0)
        activity.getApplicationDeadline().withNano(0) == SpockTest.IN_ONE_DAY.withNano(0)
        activity.themes.get(0).getName() == SpockTest.THEME_NAME_1

        cleanup:
        deleteAll()
    }

    def "login as member of another institution and cannot update"() {
        given:
        def otherInstitution = new Institution(SpockTest.INSTITUTION_1_NAME, SpockTest.INSTITUTION_1_EMAIL, SpockTest.INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_PASSWORD, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(SpockTest.USER_1_USERNAME, SpockTest.USER_1_PASSWORD)

        when:
        webClient.put()
                .uri('/activities/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
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

    def "login as volunteer, and update an activity"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'the volunteer registers the activity'
        webClient.put()
                .uri('/activities/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
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
                .uri('/activities/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
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
