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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateMemberParticipationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def volunteer
    def participationId
    def member

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        member = authUserService.loginDemoMemberAuth().getUser()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()


        def institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 3, ACTIVITY_DESCRIPTION_1,
                NOW.plusDays(1), NOW.plusDays(2), NOW.plusDays(3), null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.volunteerId = volunteer.getId()
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_1
        enrollmentDto.activityId = activity.id

        enrollmentService.createEnrollment(volunteer.id, activity.id, enrollmentDto)

        activity.setStartingDate(NOW.minusDays(4))
        activity.setEndingDate(NOW.minusDays(3))
        activity.setApplicationDeadline(NOW.minusDays(5))
        activityRepository.save(activity)

        def participationDto = new ParticipationDto()
        participationDto.memberRating = 5
        participationDto.memberReview = MEMBER_REVIEW
        participationDto.volunteerRating = 5
        participationDto.volunteerReview = VOLUNTEER_REVIEW
        participationDto.volunteerId = volunteer.id

        participationService.createParticipation(activity.id,participationDto)
        participationId = participationRepository.findAll().get(0).getId()

    }

    def 'login as a member and update a participation'() {
        given: 'a member'
        demoMemberLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.memberRating = 1
        participationDtoUpdate.memberReview = "NEW REVIEW"
        participationDtoUpdate.volunteerId = volunteer.getId()


        when: 'the member edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId + "/member")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response"
        response.memberRating == 1
        response.memberReview == "NEW REVIEW"
        and: 'check database'
        participationRepository.count() == 1
        def participation = participationRepository.findAll().get(0)
        participation.getMemberRating() == 1
        participation.getMemberReview() == "NEW REVIEW"



        cleanup:
        deleteAll()
    }

    def 'update with a rating of 10 abort and no changes'() {
        given: 'a member'
        demoMemberLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.memberRating = 10
        participationDtoUpdate.memberReview = MEMBER_REVIEW

        when: 'the member edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId + "/member")
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
        participation.getMemberRating() == 5



        cleanup:
        deleteAll()
    }

    def 'login as a member of another institution and try to edit a participation'() {
        given: 'a member from another institution'
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(USER_1_NAME,USER_1_USERNAME,USER_1_PASSWORD,USER_1_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.memberRating = 3
        participationDtoUpdate.memberReview = "ANOTHER_REVIEW"
        participationDtoUpdate.volunteerId = volunteer.id

        when: 'the member tries to edit the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId + "/member")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def participation = participationRepository.findAll().get(0)
        participation.getMemberRating() == 5
        participation.getMemberReview() ==  MEMBER_REVIEW



        cleanup:
        deleteAll()
    }

    def 'login as a member and try to rate a participation before activity end'() {
        given: 'a member and an activity that has not ended yet'
        deleteAll()
        demoMemberLogin()
        def volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        def institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_2, ACTIVITY_REGION_2, 3, ACTIVITY_DESCRIPTION_2,
                NOW.minusDays(2), NOW.minusDays(1), NOW.plusDays(2), null)
        def activity2 = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity2)

        def participationDto = new ParticipationDto()
        participationDto.memberRating = null
        participationDto.memberReview = null
        participationDto.volunteerId = volunteer.id

        participationService.createParticipation(activity2.id, participationDto)
        participationId = participationRepository.findAll().get(0).getId()


        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.memberRating = 1
        participationDtoUpdate.memberReview = "NEW REVIEW"
        participationDtoUpdate.volunteerId = volunteer.id

        when: 'the member tries to rate the participation before the activity has ended'
        def response = webClient.put()
                .uri("/participations/" + participationId + "/member")
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
        participation.getMemberRating() == null
        participation.getMemberReview() == null

        cleanup:
        deleteAll()
    }


    def 'login as a admin and try to edit a participation'() {
        given: 'a demo'
        demoAdminLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.memberRating = 1
        participationDtoUpdate.memberReview = "ANOTHER_REVIEW"
        participationDtoUpdate.volunteerId = volunteer.id

        when: 'the admin edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId + "/member")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def participation = participationRepository.findAll().get(0)
        participation.getMemberRating() ==  5
        participation.getMemberReview() == MEMBER_REVIEW


        cleanup:
        deleteAll()
    }


    def 'login as a volunteer and try to update a member rating'() {
        given: 'a demo'
        demoVolunteerLogin()
        def participationDtoUpdate = new ParticipationDto()
        participationDtoUpdate.memberRating = 1
        participationDtoUpdate.memberReview = "ANOTHER_REVIEW"
        participationDtoUpdate.volunteerId = volunteer.id

        when: 'the admin edits the participation'
        def response = webClient.put()
                .uri("/participations/" + participationId + "/member")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDtoUpdate)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        def participation = participationRepository.findAll().get(0)
        participation.getMemberRating() ==  5
        participation.getMemberReview() == MEMBER_REVIEW


        cleanup:
        deleteAll()
    }


}