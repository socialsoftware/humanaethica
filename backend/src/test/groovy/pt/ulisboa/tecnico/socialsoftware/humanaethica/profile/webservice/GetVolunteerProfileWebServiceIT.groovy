package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetVolunteerProfileWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def volunteer
    def participation
    def volunteerProfileDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "an institution with three activities"
        def institution = institutionService.getDemoInstitution()
        def activityDto1 = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)
        def activityDto2 = createActivityDto(ACTIVITY_NAME_2,ACTIVITY_REGION_2,3,ACTIVITY_DESCRIPTION_2,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)
        def activityDto3 = createActivityDto(ACTIVITY_NAME_3,ACTIVITY_REGION_3,3,ACTIVITY_DESCRIPTION_3,
                IN_ONE_DAY, IN_TWO_DAYS, IN_TEN_DAYS,null)
        def activity1 = new Activity(activityDto1, institution, new ArrayList<>())
        def activity2 = new Activity(activityDto2, institution, new ArrayList<>())
        def activity3 = new Activity(activityDto3, institution, new ArrayList<>())
        activityRepository.save(activity1)
        activityRepository.save(activity2)
        activityRepository.save(activity3)

        and: "a volunteer with two participations"
        volunteer = createVolunteerWithPassword(USER_1_NAME, USER_1_USERNAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        def participationDto = new ParticipationDto()
        participationDto.memberRating = 5
        participationDto.memberReview = MEMBER_REVIEW
        participation = createParticipation(activity1, volunteer, participationDto)
        participationDto.memberRating = 4
        createParticipation(activity2, volunteer, participationDto)

        and: "one enrollment and one assessment"
        createEnrollment(activity3, volunteer, ENROLLMENT_MOTIVATION_1)
        createAssessment(institution, volunteer, ASSESSMENT_REVIEW_1)

        volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.volunteer = new UserDto(volunteer)
        volunteerProfileDto.shortBio = VOLUNTEER_SHORT_BIO
        volunteerProfileDto.selectedParticipations = [new ParticipationDto(participation, User.Role.VOLUNTEER)]

        volunteerProfileService.createVolunteerProfile(volunteer.id, volunteerProfileDto)
    }

    def 'volunteer gets its own profile'() {
        given:
        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when:
        def response = webClient.get()
                .uri('/profile/volunteer/' + volunteer.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then:
        response.volunteer.id == volunteer.id
        response.shortBio == VOLUNTEER_SHORT_BIO
        response.numTotalEnrollments == 1
        response.numTotalParticipations == 2
        response.numTotalAssessments == 1
        response.averageRating == 4.5
        response.selectedParticipations.size() == 1
        response.selectedParticipations.get(0).id == participation.id
        and:
        volunteerProfileRepository.findAll().size() == 1
        def storedVolunteerProfile = volunteerProfileRepository.findAll().get(0)
        storedVolunteerProfile.volunteer.getId() == volunteer.id
        storedVolunteerProfile.getShortBio() == VOLUNTEER_SHORT_BIO
        storedVolunteerProfile.getNumTotalEnrollments() == 1
        storedVolunteerProfile.getNumTotalParticipations() == 2
        storedVolunteerProfile.getNumTotalAssessments() == 1
        storedVolunteerProfile.getAverageRating() == 4.5
        storedVolunteerProfile.getSelectedParticipations().size() == 1
        storedVolunteerProfile.getSelectedParticipations().get(0).getId() == participation.id

        cleanup:
        deleteAll()
    }

    def "a member gets a volunteer's profile"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/profile/volunteer/' + volunteer.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then:
        response.volunteer.id == volunteer.id
        response.shortBio == VOLUNTEER_SHORT_BIO
        response.numTotalEnrollments == 1
        response.numTotalParticipations == 2
        response.numTotalAssessments == 1
        response.averageRating == 4.5
        response.selectedParticipations.size() == 1
        response.selectedParticipations.get(0).id == participation.id
        and:
        volunteerProfileRepository.findAll().size() == 1
        def storedVolunteerProfile = volunteerProfileRepository.findAll().get(0)
        storedVolunteerProfile.volunteer.getId() == volunteer.id
        storedVolunteerProfile.getShortBio() == VOLUNTEER_SHORT_BIO
        storedVolunteerProfile.getNumTotalEnrollments() == 1
        storedVolunteerProfile.getNumTotalParticipations() == 2
        storedVolunteerProfile.getNumTotalAssessments() == 1
        storedVolunteerProfile.getAverageRating() == 4.5
        storedVolunteerProfile.getSelectedParticipations().size() == 1
        storedVolunteerProfile.getSelectedParticipations().get(0).getId() == participation.id

        cleanup:
        deleteAll()
    }

    def "a non authenticated user gets a volunteer's profile"() {
        when:
        def response = webClient.get()
                .uri('/profile/volunteer/' + volunteer.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then:
        response.volunteer.id == volunteer.id
        response.shortBio == VOLUNTEER_SHORT_BIO
        response.numTotalEnrollments == 1
        response.numTotalParticipations == 2
        response.numTotalAssessments == 1
        response.averageRating == 4.5
        response.selectedParticipations.size() == 1
        response.selectedParticipations.get(0).id == participation.id
        and:
        volunteerProfileRepository.findAll().size() == 1
        def storedVolunteerProfile = volunteerProfileRepository.findAll().get(0)
        storedVolunteerProfile.volunteer.getId() == volunteer.id
        storedVolunteerProfile.getShortBio() == VOLUNTEER_SHORT_BIO
        storedVolunteerProfile.getNumTotalEnrollments() == 1
        storedVolunteerProfile.getNumTotalParticipations() == 2
        storedVolunteerProfile.getNumTotalAssessments() == 1
        storedVolunteerProfile.getAverageRating() == 4.5
        storedVolunteerProfile.getSelectedParticipations().size() == 1
        storedVolunteerProfile.getSelectedParticipations().get(0).getId() == participation.id

        cleanup:
        deleteAll()
    }

    def 'volunteer gets non-existent profile'() {
        given: "a volunteer without profile"
        def demoVolunteerId = demoVolunteerLogin().getId()

        when: "we get their profile"
        def response = webClient.get()
                .uri('/profile/volunteer/' + demoVolunteerId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(VolunteerProfileDto.class)
                .block()

        then: "null is returned"
        response == null

        cleanup:
        deleteAll()
    }

}