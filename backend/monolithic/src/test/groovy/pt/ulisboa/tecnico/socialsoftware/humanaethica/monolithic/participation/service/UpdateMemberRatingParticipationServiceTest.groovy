package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class UpdateMemberRatingParticipationServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def activity
    def participation
    def volunteer
    def member

    def setup() {
        def institution = institutionService.getDemoInstitution()
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.State.APPROVED)
        member = createMember(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, institution,User.State.APPROVED)

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        def participationDto = new ParticipationDto()
        participationDto.volunteerRating = 5
        participationDto.volunteerReview = VOLUNTEER_REVIEW
        participationDto.volunteerId = volunteer.getId()
        participation = participationService.createParticipation(activity.id, participationDto)
    }

    def 'member updates a participation' () {
        given:
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.memberReview = MEMBER_REVIEW
        updatedParticipationDto.memberRating = 5
        updatedParticipationDto.volunteerId = volunteer.getId()

        when:
        def result = participationService.memberRating(participation.id, updatedParticipationDto)
        sleep(1)

        then: "the returned data is correct"
        result.memberReview == MEMBER_REVIEW
        result.memberRating == 5
        and: "the participation is stored"
        participationRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.memberReview == MEMBER_REVIEW
        storedParticipation.memberRating == 5
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id

    }

    @Unroll
    def 'invalid arguments: rating=#rating | review=#review |participationId=#participationId'() {
        given:
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.memberRating = rating
        updatedParticipationDto.memberReview = review


        when:
        participationService.memberRating(getParticipationId(participationId), updatedParticipationDto)

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
