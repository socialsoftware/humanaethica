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
class UpdateEnrollmentWebServiceIT extends SpockTest {
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

    def 'login as a volunteer and edit an enrollment'() {
        given: 'a volunteer'
        demoVolunteerLogin()

        def enrollmentDtoEdit = new EnrollmentDto()
        enrollmentDtoEdit.motivation = SpockTest.ENROLLMENT_MOTIVATION_2

        when: 'then volunteer edits the enrollment'
        def response = webClient.put()
                .uri('/enrollments/' + enrollmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDtoEdit)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "check response"
        response.motivation == SpockTest.ENROLLMENT_MOTIVATION_2
        and: "check database"
        enrollmentRepository.count() == 1
        def enrollment = enrollmentRepository.findAll().get(0)
        enrollment.motivation == SpockTest.ENROLLMENT_MOTIVATION_2

        cleanup:
        deleteAll()
    }

    def 'edit with another motivation abort and no changes'() {
        given: 'a volunteer'
        demoVolunteerLogin()
        and:
        def enrollmentDtoEdit = new EnrollmentDto()  
        enrollmentDtoEdit.motivation = ""

        when: 'the volunteer edits the enrollment'
        webClient.put()
                .uri('/enrollments/' + enrollmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDtoEdit)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        and: 'check database'
        enrollmentRepository.count() == 1
        def enrollment = enrollmentRepository.findAll().get(0)
        enrollment.getMotivation() == SpockTest.ENROLLMENT_MOTIVATION_1

        cleanup:
        deleteAll()
    }


    def 'login as a member and edit an enrollment'() {
        given: 'a member'
        demoMemberLogin()
        and:
        def enrollmentDtoEdit = new EnrollmentDto()
        enrollmentDtoEdit.motivation = SpockTest.ENROLLMENT_MOTIVATION_2

        when: 'the member edits the enrollment'
        webClient.put()
                .uri('/enrollments/' + enrollmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDtoEdit)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()
        
        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def enrollment = enrollmentRepository.findAll().get(0)
        enrollment.getMotivation() == SpockTest.ENROLLMENT_MOTIVATION_1

        cleanup:
        deleteAll()
    }

    def 'login as a admin and edit an enrollment'() {
        given: 'a demo'
        demoMemberLogin()
        and:
        def enrollmentDtoEdit = new EnrollmentDto()
        enrollmentDtoEdit.motivation = SpockTest.ENROLLMENT_MOTIVATION_2

        when: 'the admin edits the enrollment'
        webClient.put()
                .uri('/enrollments/' + enrollmentId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDtoEdit)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()
        
        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def enrollment = enrollmentRepository.findAll().get(0)
        enrollment.getMotivation() == SpockTest.ENROLLMENT_MOTIVATION_1

        cleanup:
        deleteAll()
    }
}