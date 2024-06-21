package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institution
    def assessmentDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        institution = institutionService.getDemoInstitution()
        def volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO,ONE_DAY_AGO,NOW,null)

        def activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
    }

    def 'volunteer create assessment'() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri('/institutions/' + institution.id + '/assessments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then:
        response.review == ASSESSMENT_REVIEW_1
        and:
        assessmentRepository.findAll().size() == 1
        def storedAssessment = assessmentRepository.findAll().get(0)
        storedAssessment.review == ASSESSMENT_REVIEW_1

        cleanup:
        deleteAll()
    }

    def 'volunteer create assessment with error'() {
        given:
        demoVolunteerLogin()
        and:
        assessmentDto.review = null

        when:
        def response = webClient.post()
                .uri('/institutions/' + institution.id + '/assessments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'member cannot create assessment'() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/institutions/' + institution.id + '/assessments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'admin cannot create assessment'() {
        given:
        demoAdminLogin()

        when:
        def response = webClient.post()
                .uri('/institutions/' + institution.id + '/assessments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }
}
