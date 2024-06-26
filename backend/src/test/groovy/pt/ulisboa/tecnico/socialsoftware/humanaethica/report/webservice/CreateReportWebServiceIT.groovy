package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateReportWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def reportDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        reportDto = new ReportDto()
        reportDto.justification = REPORT_JUSTIFICATION_1
    }

    def 'volunteer create report'() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(reportDto)
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block()

        then:
        response.justification == REPORT_JUSTIFICATION_1
        and:
        reportRepository.getReportsByActivityId(activity.id).size() == 1
        def storedReport = reportRepository.getReportsByActivityId(activity.id).get(0)
        storedReport.justification == REPORT_JUSTIFICATION_1

        cleanup:
        deleteAll()
    }

    def 'volunteer create report with error'() {
        given:
        demoVolunteerLogin()
        and:
        reportDto.justification = null

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(reportDto)
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        reportRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'member cannot create report'() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(reportDto)
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        reportRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'admin cannot create report'() {
        given:
        demoAdminLogin()

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/reports')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(reportDto)
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        reportRepository.count() == 0

        cleanup:
        deleteAll()
    }
}