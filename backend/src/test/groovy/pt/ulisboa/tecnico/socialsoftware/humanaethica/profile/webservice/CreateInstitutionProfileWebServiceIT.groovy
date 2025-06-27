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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.InstitutionProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateInstitutionProfileWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institution
    def assessmentOne
    def assessmentTwo
    def assessmentThree
    def assessmentFour
    def assessmentFive

    def institutionProfileDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "an institution with 5 members (demo user + 4 new members)"
        institution = institutionService.getDemoInstitution()

        createMember(USER_1_NAME, USER_1_USERNAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, institution, User.State.APPROVED)
        createMember(USER_2_NAME, USER_2_USERNAME, USER_2_PASSWORD, USER_2_EMAIL, AuthUser.Type.NORMAL, institution, User.State.APPROVED)
        createMember(USER_3_NAME, USER_3_USERNAME, USER_3_PASSWORD, USER_3_EMAIL, AuthUser.Type.NORMAL, institution, User.State.APPROVED)
        createMember(USER_4_NAME, USER_4_USERNAME, USER_4_PASSWORD, USER_4_EMAIL, AuthUser.Type.NORMAL, institution, User.State.APPROVED)

        and: "3 activities"
        def activityDto1 = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)
        def activityDto2 = createActivityDto(ACTIVITY_NAME_2,ACTIVITY_REGION_2,3,ACTIVITY_DESCRIPTION_2,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)
        def activityDto3 = createActivityDto(ACTIVITY_NAME_3,ACTIVITY_REGION_3,3,ACTIVITY_DESCRIPTION_3,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)

        def activity1 = new Activity(activityDto1, institution, new ArrayList<>())
        def activity2 = new Activity(activityDto2, institution, new ArrayList<>())
        def activity3 = new Activity(activityDto3, institution, new ArrayList<>())
        activityRepository.save(activity1)
        activityRepository.save(activity2)
        activityRepository.save(activity3)

        and: "5 participations by 5 volunteers with average rating of 4.8"
        def participationDto = new ParticipationDto()
        def volunteer1 = createVolunteer(USER_5_NAME, USER_5_USERNAME, USER_5_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        participationDto.volunteerRating = 5
        createParticipation(activity1, volunteer1, participationDto)

        def volunteer2 = createVolunteer(USER_6_NAME, USER_6_USERNAME, USER_6_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def volunteer3 = createVolunteer(USER_7_NAME, USER_7_USERNAME, USER_7_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        participationDto.volunteerRating = 5
        createParticipation(activity2, volunteer2, participationDto)
        participationDto.volunteerRating = 4
        createParticipation(activity2, volunteer3, participationDto)

        def volunteer4 = createVolunteer(USER_8_NAME, USER_8_USERNAME, USER_8_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        participationDto.volunteerRating = 5
        createParticipation(activity3, volunteer4, participationDto)

        def volunteer5 = createVolunteer(USER_9_NAME, USER_9_USERNAME, USER_9_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        participationDto.volunteerRating = 5
        createParticipation(activity3, volunteer5, participationDto)

        and: "5 assessments"
        assessmentFive = createAssessment(institution, volunteer5, ASSESSMENT_REVIEW_1)
        assessmentFour = createAssessment(institution, volunteer4, ASSESSMENT_REVIEW_1)
        assessmentThree = createAssessment(institution, volunteer3, ASSESSMENT_REVIEW_1)
        assessmentTwo = createAssessment(institution, volunteer2, ASSESSMENT_REVIEW_1)
        assessmentOne = createAssessment(institution, volunteer1, ASSESSMENT_REVIEW_1)

        and: "a valid institution profile DTO"
        institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = INSTITUTION_SHORT_DESC
        institutionProfileDto.selectedAssessments = [new AssessmentDto(assessmentOne), new AssessmentDto(assessmentThree), new AssessmentDto(assessmentFour)]
    }

    def 'member creates profile of its institution'() {
        given: "a logged in volunteer"
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/profile/institution')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(institutionProfileDto)
                .retrieve()
                .bodyToMono(InstitutionProfileDto.class)
                .block()

        then:
        response.institution.id == institution.id
        response.numMembers == 5
        response.numActivities == 3
        response.numAssessments == 5
        response.numVolunteers == 5
        response.averageRating == 4.8
        response.shortDescription == INSTITUTION_SHORT_DESC
        response.selectedAssessments.size() == 3
        response.selectedAssessments.collect({a -> a.getId()}).toSet() == [assessmentOne.id, assessmentThree.id, assessmentFour.id].toSet()
        and:
        institutionRepository.findAll().size() == 1
        def storedInstitutionProfile = institutionProfileRepository.findAll().get(0)
        storedInstitutionProfile.getInstitution().getId() == institution.id
        storedInstitutionProfile.getNumMembers() == 5
        storedInstitutionProfile.getNumActivities() == 3
        storedInstitutionProfile.getNumAssessments() == 5
        storedInstitutionProfile.getNumVolunteers() == 5
        storedInstitutionProfile.getAverageRating() == 4.8
        storedInstitutionProfile.getSelectedAssessments().size() == 3
        storedInstitutionProfile.selectedAssessments.collect({a -> a.getId()}).toSet() == [assessmentOne.id, assessmentThree.id, assessmentFour.id].toSet()

        cleanup:
        deleteAll()
    }

    def 'member creates profile of its institution with wrong data'() {
        given: "a logged in volunteer"
        demoMemberLogin()
        and:
        institutionProfileDto.shortDescription = "too short"

        when:
        webClient.post()
                .uri('/profile/institution')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(institutionProfileDto)
                .retrieve()
                .bodyToMono(InstitutionProfileDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        institutionProfileRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'volunteer cannot create a profile for an institution'() {
        given: "a logged in volunteer"
        demoVolunteerLogin()

        when:
        webClient.post()
                .uri('/profile/institution')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(institutionProfileDto)
                .retrieve()
                .bodyToMono(InstitutionProfileDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        institutionProfileRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def 'admin cannot create a profile for an institution'() {
        given: "a logged in volunteer"
        demoAdminLogin()

        when:
        webClient.post()
                .uri('/profile/institution')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(institutionProfileDto)
                .retrieve()
                .bodyToMono(InstitutionProfileDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        institutionProfileRepository.count() == 0

        cleanup:
        deleteAll()
    }
}