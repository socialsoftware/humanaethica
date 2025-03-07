package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.assessment

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteAssessmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def volunteer
    def assessmentId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.THREE_DAYS_AGO, SpockTest.TWO_DAYS_AGO, SpockTest.ONE_DAY_AGO, null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
        institution.addActivity(activity)

        def assessmentDto = new AssessmentDto()
        assessmentDto.review = SpockTest.ASSESSMENT_REVIEW_1
        assessmentDto.volunteerId = volunteer.id

        assessmentService.createAssessment(volunteer.id ,institution.id, assessmentDto)

        def storedAssessment = assessmentRepository.findAll().get(0)
        assessmentId = storedAssessment.id
    }

    def 'login as a volunteer and remove an assessment'() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'then volunteer deletes the assessment'
        def response = webClient.delete()
                .uri("/assessments/" + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response"
        response.review == SpockTest.ASSESSMENT_REVIEW_1
        and: "check database"
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'login as a volunteer that did not do the assessment'() {
        given: 'another volunteer'
        def otherInstitution = new Institution(SpockTest.INSTITUTION_1_NAME, SpockTest.INSTITUTION_1_EMAIL, SpockTest.INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def volunteer = createVolunteer(SpockTest.USER_2_NAME, SpockTest.USER_2_USERNAME, SpockTest.USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        volunteer.authUser.setPassword(passwordEncoder.encode(SpockTest.USER_2_PASSWORD))
        userRepository.save(volunteer)
        normalUserLogin(SpockTest.USER_2_USERNAME, SpockTest.USER_2_PASSWORD)

        when: 'the other volunteer deletes the assessment'
        webClient.delete()
                .uri("/assessments/" + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: 'check response status'
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: 'check database'
        assessmentRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def 'login as a member and try to delete an assessment'() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member deletes the assessment'
        webClient.delete()
                .uri("/assessments/" + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "check database"
        assessmentRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def 'login as a admin and try to delete an assessment'() {
        given: 'a admin'
        demoAdminLogin()

        when: 'the admin deletes the assessment'
        webClient.delete()
                .uri("/assessments/" + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "check database"
        assessmentRepository.count() == 1

        cleanup:
        deleteAll()
    }
}