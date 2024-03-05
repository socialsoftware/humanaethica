package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetEnrollmentsByActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity

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

        def volunteerOne = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def volunteerTwo = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createEnrollment(activity, volunteerOne, ENROLLMENT_MOTIVATION_1)
        createEnrollment(activity, volunteerTwo, ENROLLMENT_MOTIVATION_2)
    }

    def 'member gets two enrollments'() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        response.size() == 2
        response.get(0).motivation == ENROLLMENT_MOTIVATION_1
        response.get(1).motivation == ENROLLMENT_MOTIVATION_2
    }

    def 'member of another institution cannot get enrollments'() {
        given:
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        createMember(USER_3_NAME,USER_3_USERNAME,USER_3_PASSWORD,USER_3_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_3_USERNAME, USER_3_PASSWORD)

        when:
        webClient.get()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def 'volunteer cannot get enrollments'() {
        given:
        demoVolunteerLogin()

        when:
        webClient.get()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def 'admin cannot get enrollments'() {
        given:
        demoAdminLogin()

        when:
        webClient.get()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def 'activity does not exist'() {
        given:
        demoMemberLogin()

        when:
        webClient.get()
                .uri('/activities/' + 222 + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }
}
