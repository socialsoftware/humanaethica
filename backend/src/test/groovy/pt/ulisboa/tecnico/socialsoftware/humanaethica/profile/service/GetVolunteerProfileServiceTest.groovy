package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.VolunteerProfile
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import spock.lang.Unroll

@DataJpaTest
class GetVolunteerProfileServiceTest extends SpockTest {
    def volunteer
    def participation

    def setup() {
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
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        def participationDto = new ParticipationDto()
        participationDto.memberRating = 5
        participationDto.memberReview = MEMBER_REVIEW
        participation = createParticipation(activity1, volunteer, participationDto)
        participationDto.memberRating = 4
        createParticipation(activity2, volunteer, participationDto)

        and: "one enrollment and one assessment"
        createEnrollment(activity3, volunteer, ENROLLMENT_MOTIVATION_1)
        createAssessment(institution, volunteer, ASSESSMENT_REVIEW_1)
    }

    def "get a volunteer's profile"() {
        given: "information for a new volunteer profile"
        def volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.volunteer = new UserDto(volunteer)
        volunteerProfileDto.shortBio = VOLUNTEER_SHORT_BIO
        volunteerProfileDto.selectedParticipations = [new ParticipationDto(participation, User.Role.VOLUNTEER)]

        and: "a new volunteer profile with that information"
        def volunteerProfile = new VolunteerProfile(volunteer, volunteerProfileDto)
        volunteerProfileRepository.save(volunteerProfile)

        when: "we get the volunteer profile for this volunteer"
        def result = volunteerProfileService.getVolunteerProfile(volunteer.id).get()


        then: "the information is correct"
        result.volunteer.id == volunteer.id
        result.shortBio == VOLUNTEER_SHORT_BIO
        result.numTotalEnrollments == 1
        result.numTotalParticipations == 2
        result.numTotalAssessments == 1
        result.averageRating == 4.5
        result.selectedParticipations.size() == 1
        result.selectedParticipations.get(0).id == participation.id
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
    }

    @Unroll
    def 'get the profile of a volunteer without profile'() {
        when:
        def result = volunteerProfileService.getVolunteerProfile(authUserService.loginDemoMemberAuth().getUser().getId())

        then:
        result == Optional.empty()
    }

    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId'() {
        when:
        volunteerProfileService.getVolunteerProfile(getProfileObjectId(volunteerId, volunteer, authUserService.loginDemoMemberAuth().getUser()))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        volunteerProfileRepository.findAll().size() == 0

        where:
        volunteerId         || errorMessage
        null                || ErrorMessage.USER_NOT_FOUND
        NO_EXIST            || ErrorMessage.USER_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}