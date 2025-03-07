package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.enrollment

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.dto.EnrollmentDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateEnrollmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def enrollmentDto

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

        enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = SpockTest.ENROLLMENT_MOTIVATION_1
    }

    def 'volunteer create enrollment'() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then:
        response.motivation == SpockTest.ENROLLMENT_MOTIVATION_1
        and:
        enrollmentRepository.getEnrollmentsByActivityId(activity.id).size() == 1
        def storedEnrollment = enrollmentRepository.getEnrollmentsByActivityId(activity.id).get(0)
        storedEnrollment.motivation == SpockTest.ENROLLMENT_MOTIVATION_1

        cleanup:
        deleteAll()
    }

    def 'volunteer create enrollment with error'() {
        given:
        demoVolunteerLogin()
        and:
        enrollmentDto.motivation = null

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'member cannot create enrollment'() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'admin cannot create enrollment'() {
        given:
        demoAdminLogin()

        when:
        def response = webClient.post()
                .uri('/activities/' + activity.id + '/enrollments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }
}
