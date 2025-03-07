package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.report

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.report.dto.ReportDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetVolunteerReportsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def volunteer

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()

        def activityDtoOne = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)

        def activityOne = new Activity(activityDtoOne, institution, new ArrayList<>())
        activityRepository.save(activityOne)

        def activityDtoTwo = createActivityDto(SpockTest.ACTIVITY_NAME_2, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)

        def activityTwo = new Activity(activityDtoTwo, institution, new ArrayList<>())
        activityRepository.save(activityTwo)

        volunteer = createVolunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createReport(activityOne, volunteer, SpockTest.REPORT_JUSTIFICATION_1)
        createReport(activityTwo, volunteer, SpockTest.REPORT_JUSTIFICATION_2)
    }

    def "volunteer cannot get reports"(){
        given:
        demoVolunteerLogin()

        when:
        webClient.get()
                .uri('/volunteers/' + volunteer.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ReportDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def "member cannot get reports"(){
        given:
        demoMemberLogin()

        when:
        webClient.get()
                .uri('/volunteers/' + volunteer.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ReportDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def "admin can get reports"(){
        given:
        demoAdminLogin()

        when:
        def response = webClient.get()
                .uri('/volunteers/' + volunteer.id + '/reports')
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