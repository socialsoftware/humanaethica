package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.enrollment

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.enrollment.dto.EnrollmentDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteEnrollmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def volunteer
    def enrollmentId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,2, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.IN_ONE_DAY, SpockTest.IN_TWO_DAYS, SpockTest.IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = SpockTest.ENROLLMENT_MOTIVATION_1
        enrollmentDto.volunteerId = volunteer.id

        enrollmentService.createEnrollment(volunteer.id ,activity.id, enrollmentDto)

        def storedEnrollment = enrollmentRepository.findAll().get(0)
        enrollmentId = storedEnrollment.id
    }

    def 'login as a volunteer and remove an enrollment'() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'then volunteer deletes the enrollment'
        def response = webClient.delete()
                .uri("/enrollments/" + enrollmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "check response"
        response.motivation == SpockTest.ENROLLMENT_MOTIVATION_1
        and: "check database"
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'login as a member and try to delete an enrollment'() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member deletes the enrollment'
        webClient.delete()
                .uri("/enrollments/" + enrollmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()
        
        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "check database"
        enrollmentRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def 'login as a admin and try to delete an enrollment'() {
        given: 'a admin'
        demoAdminLogin()

        when: 'the admin deletes the enrollment'
        webClient.delete()
                .uri("/enrollments/" + enrollmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()
        
        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: "check database"
        enrollmentRepository.count() == 1

        cleanup:
        deleteAll()
    }
}