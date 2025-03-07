package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.enrollment

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;

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

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,1, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def volunteerOne = createVolunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def volunteerTwo = createVolunteer(SpockTest.USER_2_NAME, SpockTest.USER_2_USERNAME, SpockTest.USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createEnrollment(activity, volunteerOne, SpockTest.ENROLLMENT_MOTIVATION_1)
        createEnrollment(activity, volunteerTwo, SpockTest.ENROLLMENT_MOTIVATION_2)
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
        response.get(0).motivation == SpockTest.ENROLLMENT_MOTIVATION_1
        response.get(1).motivation == SpockTest.ENROLLMENT_MOTIVATION_2
    }

    def 'member of another institution cannot get enrollments'() {
        given:
        def otherInstitution = new Institution(SpockTest.INSTITUTION_1_NAME, SpockTest.INSTITUTION_1_EMAIL, SpockTest.INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        createMember(SpockTest.USER_3_NAME, SpockTest.USER_3_USERNAME, SpockTest.USER_3_PASSWORD, SpockTest.USER_3_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(SpockTest.USER_3_USERNAME, SpockTest.USER_3_PASSWORD)

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
