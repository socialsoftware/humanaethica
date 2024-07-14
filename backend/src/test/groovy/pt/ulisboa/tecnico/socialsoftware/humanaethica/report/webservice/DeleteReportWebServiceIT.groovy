package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteReportWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def volunteer
    def reportId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def reportDto = new ReportDto()
        reportDto.justification = REPORT_JUSTIFICATION_1
        reportDto.volunteerId = volunteer.id

        reportService.createReport(volunteer.id ,activity.id, reportDto)

        def storedReport = reportRepository.findAll().get(0)
        reportId = storedReport.id
    }

    def 'login as a volunteer and remove an report'() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'then volunteer deletes the report'
        def response = webClient.delete()
                .uri("/reports/" + reportId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block()

        then: "check response"
        response.justification == REPORT_JUSTIFICATION_1
        and: "check database"
        reportRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'login as a member and try to delete an report'() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member deletes the report'
        webClient.delete()
                .uri("/reports/" + reportId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block()
        
        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "check database"
        reportRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def 'login as a admin and try to delete an report'() {
        given: 'a admin'
        demoAdminLogin()

        when: 'the admin deletes the report'
        webClient.delete()
                .uri("/reports/" + reportId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ReportDto.class)
                .block()
        
        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "check database"
        reportRepository.count() == 1

        cleanup:
        deleteAll()
    }
}