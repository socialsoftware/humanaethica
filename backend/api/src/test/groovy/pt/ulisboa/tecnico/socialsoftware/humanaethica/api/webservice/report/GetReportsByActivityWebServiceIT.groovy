package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.report

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.report.dto.ReportDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetReportsByActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def volunteerOne = createVolunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def volunteerTwo = createVolunteer(SpockTest.USER_2_NAME, SpockTest.USER_2_USERNAME, SpockTest.USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createReport(activity, volunteerOne, SpockTest.REPORT_JUSTIFICATION_1)
        createReport(activity, volunteerTwo, SpockTest.REPORT_JUSTIFICATION_2)
    }

    def 'member cannot get reports'() {
        given:
        demoMemberLogin()

        when:
        webClient.get()
                .uri('/activities/' + activity.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ReportDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def 'volunteer cannot get reports'() {
        given:
        demoVolunteerLogin()

        when:
        webClient.get()
                .uri('/activities/' + activity.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ReportDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def 'admin gets two reports'() {
        given:
        demoAdminLogin()

        when:
        def response = webClient.get()
                .uri('/activities/' + activity.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ReportDto.class)
                .collectList()
                .block()

        then:
        response.size() == 2
        response.get(0).justification == SpockTest.REPORT_JUSTIFICATION_1
        response.get(1).justification == SpockTest.REPORT_JUSTIFICATION_2
    }

}
