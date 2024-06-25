package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


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

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                THREE_DAYS_AGO,TWO_DAYS_AGO, ONE_DAY_AGO, null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
        institution.addActivity(activity)

        def assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
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
        response.review == ASSESSMENT_REVIEW_1
        and: "check database"
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'login as a volunteer that did not do the assessment'() {
        given: 'another volunteer'
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def volunteer = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        volunteer.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(volunteer)
        normalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

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