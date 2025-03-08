package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.InstitutionProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import spock.lang.Unroll

@DataJpaTest
class CreateInstitutionProfileServiceTest extends SpockTest {
    def institution
    def assessmentOne
    def assessmentTwo
    def assessmentThree
    def assessmentFour
    def assessmentFive

    def setup() {
        given: "an institution with 4 members"
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

    }

    def 'create institution profile' () {
        given:
        def institutionProfileDto = new InstitutionProfileDto()
        institutionProfileDto.shortDescription = INSTITUTION_SHORT_DESC
        institutionProfileDto.selectedAssessments = [new AssessmentDto(assessmentOne), new AssessmentDto(assessmentThree), new AssessmentDto(assessmentFour)]

        when:
        def result = institutionProfileService.createInstitutionProfile(institution.id, institutionProfileDto)

        then:
        result.institution.id == institution.id
        result.numMembers == 4
        result.numActivities == 3
        result.numAssessments == 5
        result.numVolunteers == 5
        result.averageRating == 4.8
        result.shortDescription == INSTITUTION_SHORT_DESC
        result.selectedAssessments.size() == 3
        result.selectedAssessments.collect({a -> a.getId()}).toSet() == [assessmentOne.id, assessmentThree.id, assessmentFour.id].toSet()
        and:
        institutionRepository.findAll().size() == 1
        def storedInstitutionProfile = institutionProfileRepository.findAll().get(0)
        storedInstitutionProfile.getInstitution().getId() == institution.id
        storedInstitutionProfile.getNumMembers() == 4
        storedInstitutionProfile.getNumActivities() == 3
        storedInstitutionProfile.getNumAssessments() == 5
        storedInstitutionProfile.getNumVolunteers() == 5
        storedInstitutionProfile.getAverageRating() == 4.8
        storedInstitutionProfile.getSelectedAssessments().size() == 3
        storedInstitutionProfile.selectedAssessments.collect({a -> a.getId()}).toSet() == [assessmentOne.id, assessmentThree.id, assessmentFour.id].toSet()
    }


    @Unroll
    def 'invalid arguments: institutionId=#institutionId | dtoValue=#dtoValue'() {
        given:
        def institutionProfileDto = new InstitutionProfileDto()

        when:
        institutionProfileService.createInstitutionProfile(getProfileObjectId(institutionId, institution), getExistingOrNullDto(dtoValue, institutionProfileDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        institutionProfileRepository.findAll().size() == 0

        where:
        institutionId |  dtoValue || errorMessage
        null          |  EXIST     || ErrorMessage.INSTITUTION_NOT_FOUND
        NO_EXIST      |  EXIST     || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST         |  null      || ErrorMessage.INSTITUTION_PROFILE_SHORT_DESC_TOO_SHORT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}