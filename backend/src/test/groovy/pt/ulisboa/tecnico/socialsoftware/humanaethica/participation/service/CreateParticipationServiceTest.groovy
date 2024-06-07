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
    def activity

    def setup() {
        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                THREE_DAYS_AGO,TWO_DAYS_AGO, ONE_DAY_AGO, null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
    }

    def 'create participant' () {
        given:
        def participationDto = new ParticipationDto()
        participationDto.rating = 5
        participationDto.volunteerId = volunteer.id

        when:
        def result = participationService.createParticipation(activity.id, participationDto)

        then:
        result.rating == 5
        and:
        participationRepository.findAll().size() == 1
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.rating == 5
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | activityId=#activityId'() {
        given:
        def participationDto = new ParticipationDto()
        participationDto.rating = 5
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
