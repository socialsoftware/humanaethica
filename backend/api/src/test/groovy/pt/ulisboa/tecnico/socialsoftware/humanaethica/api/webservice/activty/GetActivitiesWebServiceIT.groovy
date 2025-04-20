package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.activty

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetActivitiesWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        given: "activity info"
        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(SpockTest.THEME_NAME_1, Theme.State.APPROVED,null))
        and: "an activity"
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        and: 'another activity'
        activityDto.name = SpockTest.ACTIVITY_NAME_2
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "get activities"() {
        when:
        def response = webClient.get()
                .uri('/activities')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ActivityDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 2
        response.get(1).name == SpockTest.ACTIVITY_NAME_2
        response.get(1).region == SpockTest.ACTIVITY_REGION_1
        response.get(1).participantsNumberLimit == 1
        response.get(1).description == SpockTest.ACTIVITY_DESCRIPTION_1
        DateHandler.toLocalDateTime(response.get(1).startingDate).withNano(0) == SpockTest.IN_TWO_DAYS.withNano(0)
        DateHandler.toLocalDateTime(response.get(1).endingDate).withNano(0) == SpockTest.IN_THREE_DAYS.withNano(0)
        DateHandler.toLocalDateTime(response.get(1).applicationDeadline).withNano(0) == SpockTest.IN_ONE_DAY.withNano(0)
        response.get(1).themes.size() == 1


        cleanup:
        deleteAll()
    }
}
