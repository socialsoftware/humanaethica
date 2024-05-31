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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateParticipationWebServiceIT extends SpockTest {
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

        def participationDto = new ParticipationDto()
        participationDto.rating = 5
        participationDto.volunteerId = volunteer.id

        participationService.createParticipation(activity.id, participationDto)

        def storedParticipation = participationRepository.findAll().get(0)
        participationId = storedParticipation.id


    }

    def 'login as a member and edit a participation'() {
        given: 'a member'
        demoMemberLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.rating = 1

        when: 'the member edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response"
        response.rating == 1
        and: 'check database'
        participationRepository.count() == 1
        def participation = participationRepository.findAll().get(0)
        participation.getRating() == 1


        cleanup:
        deleteAll()
    }

    def 'update with a rating of 10 abort and no changes'() {
        given: 'a member'
        demoMemberLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.rating = 10

        when: 'the member edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        and: 'check database'
        participationRepository.count() == 1
        def participation = participationRepository.findAll().get(0)
        participation.getRating() == 5


        cleanup:
        deleteAll()
    }

    def 'login as a member of another institution and try to edit a participation'() {
        given: 'a member'
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(USER_1_NAME,USER_1_USERNAME,USER_1_PASSWORD,USER_1_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.rating = 1

        when: 'the member tries to edit the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def participation = participationRepository.findAll().get(0)
        participation.getRating() == 5


        cleanup:
        deleteAll()
    }

    def 'login as a volunteer and try to edit a participation'() {
        given: 'a volunteer'
        demoVolunteerLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.rating = 1

        when: 'the volunteer edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def participation = participationRepository.findAll().get(0)
        participation.getRating() == 5


        cleanup:
        deleteAll()
    }

    def 'login as a admin and try to edit a participation'() {
        given: 'a demo'
        demoAdminLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.rating = 1

        when: 'the admin edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def participation = participationRepository.findAll().get(0)
        participation.getRating() == 5


        cleanup:
        deleteAll()
    }

}