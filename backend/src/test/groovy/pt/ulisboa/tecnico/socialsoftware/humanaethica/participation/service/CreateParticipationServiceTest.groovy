package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateParticipationServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def volunteer
    def member
    def activity



    def setup() {
        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        member = authUserService.loginDemoMemberAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                TWO_DAYS_AGO, ONE_DAY_AGO, NOW,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
    }

    def 'create participation as member' () {
        given:
        def participationDto = new ParticipationDto()
        participationDto.memberRating = 5
        participationDto.memberReview = MEMBER_REVIEW
        participationDto.volunteerId = volunteer.id

        when:
        def result = participationService.createParticipation(activity.id, participationDto)

        then:
        result.memberRating == 5
        result.memberReview == MEMBER_REVIEW
        and:
        participationRepository.findAll().size() == 1
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.memberRating == 5
        storedParticipation.memberReview == MEMBER_REVIEW
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id
    }


    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | activityId=#activityId'() {
        given:
        def participationDto = new ParticipationDto()
        participationDto.memberRating = 5
        participationDto.memberReview = MEMBER_REVIEW
        participationDto.volunteerId = getVolunteerId(volunteerId)

        when:
        participationService.createParticipation(getActivityId(activityId), getParticipationDto(participationValue,participationDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        participationRepository.findAll().size() == 0

        where:
        volunteerId | activityId | participationValue || errorMessage
        null        | EXIST      | EXIST              || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST      | EXIST              || ErrorMessage.USER_NOT_FOUND
        EXIST       | null       | EXIST              || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | NO_EXIST   | EXIST              || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | EXIST      | null               || ErrorMessage.PARTICIPATION_REQUIRES_INFORMATION
    }

    @Unroll
    def 'invalid arguments: rating=#rating | review=#review'() {
        given:
        def participationDto = new ParticipationDto()
        participationDto.volunteerReview = review
        participationDto.volunteerRating = rating
        participationDto.volunteerId = volunteer.id

        when:
        participationService.createParticipation(activity.id, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        participationRepository.findAll().size() == 0

        where:
        review                              | rating || errorMessage
        "A".repeat(MAX_REVIEW_LENGTH + 1)   | 5      || ErrorMessage.PARTICIPATION_REVIEW_LENGTH_INVALID
        ""                                  | 5      || ErrorMessage.PARTICIPATION_REVIEW_LENGTH_INVALID
        VOLUNTEER_REVIEW                    | -1     || ErrorMessage.PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE
        VOLUNTEER_REVIEW                    | 10     || ErrorMessage.PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE
    }

    def getVolunteerId(volunteerId) {
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        else
            return null
    }

    def getActivityId(activityId) {
        if (activityId == EXIST)
            return activity.id
        else if (activityId == NO_EXIST)
            return 222
        else
            return null
    }

    def getParticipationDto(value, participationDto) {
        if (value == EXIST) {
            return participationDto
        }
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
