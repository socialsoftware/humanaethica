package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeleteParticipationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def volunteer
    def participationId


    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 3, ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW, null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def participationDto= new ParticipationDto()
        participationDto.volunteerRating = 5
        participationDto.volunteerReview = VOLUNTEER_REVIEW
        participationDto.volunteerId = volunteer.id


        participationService.createParticipation(activity.id, participationDto)

        def storedParticipation = participationRepository.findAll().get(0)
        participationId = storedParticipation.id

    }

    def 'login as a member and delete a participation'() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member deletes the participation'
        def response = webClient.delete()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response"
        response.volunteerRating == 5
        and: 'check database'
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'delete a participation that does not exist'() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member deletes the participation'
        webClient.delete()
                .uri("/participations/" + 222)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: 'check database'
        participationRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def 'login as a member of another institution and try to delete a participation'() {
        given: 'a member'
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(USER_1_NAME,USER_1_USERNAME,USER_1_PASSWORD,USER_1_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when: 'the member deletes the participation'
        webClient.delete()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: 'check database'
        participationRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def 'login as volunteer and delete a participation'() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'the volunteer tries to delete the participation'
       webClient.delete()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: 'check database'
        participationRepository.count() == 1

        cleanup:
        deleteAll()
    }

    def 'login as a admin and delete a participation'() {
        given: 'a admin'
        demoAdminLogin()

        when: 'the admin tries to delete the participation'
        webClient.delete()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        and: 'check database'
        participationRepository.count() == 1

        cleanup:
        deleteAll()
    }
}