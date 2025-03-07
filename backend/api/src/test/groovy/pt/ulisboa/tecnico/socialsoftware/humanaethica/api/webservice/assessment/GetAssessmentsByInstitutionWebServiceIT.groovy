package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.assessment

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAssessmentsByInstitutionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institution

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.TWO_DAYS_AGO, SpockTest.ONE_DAY_AGO, SpockTest.NOW,null)

        def activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
        institution.addActivity(activity)

        def volunteerOne = createVolunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def volunteerTwo = createVolunteer(SpockTest.USER_2_NAME, SpockTest.USER_2_USERNAME, SpockTest.USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        createAssessment(institution, volunteerOne, SpockTest.ASSESSMENT_REVIEW_1)
        createAssessment(institution, volunteerTwo, SpockTest.ASSESSMENT_REVIEW_2)
    }

    def 'non authenticated user gets two assessment'() {
        when:
        def response = webClient.get()
                .uri('/institutions/' + institution.id + '/assessments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then:
        response.size() == 2
        response.get(0).review == SpockTest.ASSESSMENT_REVIEW_1
        response.get(0).getReviewDate() != null
        response.get(1).review == SpockTest.ASSESSMENT_REVIEW_2
        response.get(0).getReviewDate() != null
    }

    def 'institution does not exist'() {
        when:
        def response = webClient.get()
                .uri('/institutions/' + '222' + '/assessments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
    }
}
