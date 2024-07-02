package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.web.servlet.view.RedirectView
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.PARTICIPATION_MEMBER_REVIEW_NOT_ALLOWED
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.PARTICIPATION_VOLUNTEER_IS_ALREADY_PARTICIPATING
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.PARTICIPATION_VOLUNTEER_REVIEW_NOT_ALLOWED

@DataJpaTest
class UpdateParticipationServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def activity
    def participation
    def volunteer
    def member
    def participationDtoMember
    def participationDtoVolunteer

    def setup() {
        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        member = authUserService.loginDemoMemberAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        participationDtoMember = new ParticipationDto()
        participationDtoMember.memberRating = 5
        participationDtoMember.memberReview = MEMBER_REVIEW
        participationDtoMember.volunteerId = volunteer.getId()

        participationDtoVolunteer = new ParticipationDto()
        participationDtoVolunteer.volunteerRating = 5
        participationDtoVolunteer.volunteerReview = VOLUNTEER_REVIEW
        participationDtoVolunteer.volunteerId = volunteer.getId()



    }

    def 'member updates a participation created by a volunteer' () {
        given:
        participationService.createParticipation(activity.id, participationDtoVolunteer, volunteer.id)
        participation = participationRepository.findAll().get(0)
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.memberReview = MEMBER_REVIEW
        updatedParticipationDto.volunteerId = volunteer.getId()

        when:
        def result = participationService.updateParticipation(participation.id, updatedParticipationDto, member.getId())

        then: "the returned data is correct"
        result.memberReview == MEMBER_REVIEW
        result.volunteerRating == 5
        result.volunteerReview == VOLUNTEER_REVIEW
        and: "the participation is stored"
        participationRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.memberReview == MEMBER_REVIEW
        storedParticipation.volunteerRating == 5
        storedParticipation.volunteerReview == VOLUNTEER_REVIEW
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id

    }

    def 'volunteer updates a participation created by a member' () {
        given:
        participationService.createParticipation(activity.id, participationDtoMember, member.getId())
        participation = participationRepository.findAll().get(0)
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.volunteerReview = VOLUNTEER_REVIEW
        updatedParticipationDto.volunteerRating = 3
        updatedParticipationDto.volunteerId = volunteer.getId()



        when:
        def result = participationService.updateParticipation(participation.id, updatedParticipationDto, volunteer.getId())

        then: "the returned data is correct"
        result.memberReview == MEMBER_REVIEW
        result.memberRating == 5
        result.volunteerRating == 3
        result.volunteerReview == VOLUNTEER_REVIEW
        and: "the participation is stored"
        participationRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.memberReview == MEMBER_REVIEW
        storedParticipation.memberRating == 5
        storedParticipation.volunteerRating == 3
        storedParticipation.volunteerReview == VOLUNTEER_REVIEW
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id

    }

    def 'member try to update a participation with a fake volunteer review' () {
        given:
        participationService.createParticipation(activity.id, participationDtoVolunteer, volunteer.id)
        participation = participationRepository.findAll().get(0)
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.volunteerReview = MEMBER_REVIEW
        updatedParticipationDto.volunteerRating = 1
        updatedParticipationDto.volunteerId = volunteer.getId()

        when:
        def result = participationService.updateParticipation(participation.id, updatedParticipationDto, member.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == PARTICIPATION_VOLUNTEER_REVIEW_NOT_ALLOWED
        and: "the stored participation remains the same"
        participationRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.memberReview == null
        storedParticipation.memberRating == null
        storedParticipation.volunteerRating == 5
        storedParticipation.volunteerReview == VOLUNTEER_REVIEW
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id

    }

    def 'volunteer try to update a participation with a fake member review' () {
        given:
        participationService.createParticipation(activity.id, participationDtoMember, member.id)
        participation = participationRepository.findAll().get(0)
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.memberReview = VOLUNTEER_REVIEW
        updatedParticipationDto.volunteerId = volunteer.getId()

        when:
        def result = participationService.updateParticipation(participation.id, updatedParticipationDto, volunteer.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == PARTICIPATION_MEMBER_REVIEW_NOT_ALLOWED
        and: "the stored participation remains the same"
        participationRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.memberReview == MEMBER_REVIEW
        storedParticipation.memberRating == 5
        storedParticipation.volunteerRating == null
        storedParticipation.volunteerReview == null
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id


    }

    @Unroll
    def 'invalid arguments: rating=#rating | review=#review |participationId=#participationId'() {
        given:
        participationService.createParticipation(activity.id, participationDtoVolunteer, volunteer.id)
        participation = participationRepository.findAll().get(0)
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.memberRating = rating
        updatedParticipationDto.memberReview = review


        when:
        participationService.updateParticipation(getParticipationId(participationId), updatedParticipationDto, member.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "the participation is in the database"
        participationRepository.findAll().size() == 1

        where:
        rating          | participationId   | review          || errorMessage
        -2              | EXIST             | MEMBER_REVIEW   || ErrorMessage.PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE
        3               | null              | MEMBER_REVIEW   || ErrorMessage.PARTICIPATION_NOT_FOUND
        3               | NO_EXIST          | MEMBER_REVIEW   || ErrorMessage.PARTICIPATION_NOT_FOUND
        3               | EXIST             | ""              || ErrorMessage.PARTICIPATION_REVIEW_LENGTH_INVALID
        3               | EXIST             | "a".repeat(110) || ErrorMessage.PARTICIPATION_REVIEW_LENGTH_INVALID

    }

    def getParticipationId(participationId){
        if (participationId == EXIST)
            return participation.id
        else if (participationId == NO_EXIST)
            return 222
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
