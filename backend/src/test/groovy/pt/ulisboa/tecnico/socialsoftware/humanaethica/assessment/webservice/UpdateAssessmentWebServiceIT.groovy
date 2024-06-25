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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateAssessmentWebServiceIT extends SpockTest {
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

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                THREE_DAYS_AGO,TWO_DAYS_AGO, ONE_DAY_AGO, null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
        assessmentDto.volunteerId = volunteer.id

        assessmentService.createAssessment(volunteer.id ,institution.id, assessmentDto)

        def storedAssessment = assessmentRepository.findAll().get(0)
        assessmentId = storedAssessment.id
    }

    def 'login as a volunteer and edit an assessment'() {
        given: 'a volunteer'
        demoVolunteerLogin()

        def assessmentDtoEdit = new AssessmentDto()
        assessmentDtoEdit.review = ASSESSMENT_REVIEW_2

        when: 'then volunteer edits the assessment'
        def response = webClient.put()
                .uri('/assessments/' + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDtoEdit)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response"
        response.review == ASSESSMENT_REVIEW_2
        and: "check database"
        assessmentRepository.count() == 1
        def assessment = assessmentRepository.findAll().get(0)
        assessment.review == ASSESSMENT_REVIEW_2

        cleanup:
        deleteAll()
    }

    def 'login as a member and edit an assessment'() {
        given: 'a member'
        demoMemberLogin()
        and:
        def assessmentDtoEdit = new AssessmentDto()
        assessmentDtoEdit.review = ASSESSMENT_REVIEW_2

        when: 'the member edits the assessment'
        webClient.put()
                .uri('/assessments/' + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDtoEdit)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def assessment = assessmentRepository.findAll().get(0)
        assessment.getReview() == ASSESSMENT_REVIEW_1

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

        def assessmentDtoEdit = new AssessmentDto()
        assessmentDtoEdit.review = ASSESSMENT_REVIEW_2

        when: 'the other volunteer update the assessment'
        webClient.put()
                .uri('/assessments/' + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDtoEdit)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }

    def 'login as a admin and edit an assessment'() {
        given: 'a demo'
        demoMemberLogin()
        and:
        def assessmentDtoEdit = new AssessmentDto()
        assessmentDtoEdit.review = ASSESSMENT_REVIEW_2

        when: 'the admin edits the assessment'
        webClient.put()
                .uri('/assessments/' + assessmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDtoEdit)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def assessment = assessmentRepository.findAll().get(0)
        assessment.getReview() == ASSESSMENT_REVIEW_1

        cleanup:
        deleteAll()
    }
}