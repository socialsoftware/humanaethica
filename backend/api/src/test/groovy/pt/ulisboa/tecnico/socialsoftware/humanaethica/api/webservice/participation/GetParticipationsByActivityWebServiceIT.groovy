package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.webservice.participation

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.api.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetParticipationsByActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(SpockTest.ACTIVITY_NAME_1, SpockTest.ACTIVITY_REGION_1,3, SpockTest.ACTIVITY_DESCRIPTION_1,
                SpockTest.TWO_DAYS_AGO, SpockTest.ONE_DAY_AGO, SpockTest.NOW,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def volunteerOne = createVolunteer(SpockTest.USER_1_NAME, SpockTest.USER_1_USERNAME, SpockTest.USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def volunteerTwo = createVolunteer(SpockTest.USER_2_NAME, SpockTest.USER_2_USERNAME, SpockTest.USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        def participationDto1 = new ParticipationDto()
        participationDto1.memberRating = 1
        participationDto1.memberReview = SpockTest.MEMBER_REVIEW
        def participationDto2 = new ParticipationDto()
        participationDto2.memberRating = 2
        participationDto2.memberReview = SpockTest.MEMBER_REVIEW

        and:
        createParticipation(activity, volunteerOne, participationDto1)
        createParticipation(activity, volunteerTwo, participationDto2)
    }

    def 'member gets two participations'() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/activities/' + activity.id + '/participations')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then:
        response.size() == 2
        response.get(0).memberRating == 1
        response.get(1).memberRating == 2
    }

    def 'member of another institution cannot get participations'() {
        given:
        def otherInstitution = new Institution(SpockTest.INSTITUTION_1_NAME, SpockTest.INSTITUTION_1_EMAIL, SpockTest.INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        createMember(SpockTest.USER_3_NAME, SpockTest.USER_3_USERNAME, SpockTest.USER_3_PASSWORD, SpockTest.USER_3_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(SpockTest.USER_3_USERNAME, SpockTest.USER_3_PASSWORD)

        when:
       webClient.get()
                .uri('/activities/' + activity.id + '/participations')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def 'volunteer cannot get participations'() {
        given:
        demoVolunteerLogin()

        when:
        webClient.get()
                .uri('/activities/' + activity.id + '/participations')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def 'admin cannot get participations'() {
        given:
        demoAdminLogin()

        when:
        def response = webClient.get()
                .uri('/activities/' + activity.id + '/participations')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
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
        def response = webClient.get()
                .uri('/activities/' + 222 + '/participations')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }
}
