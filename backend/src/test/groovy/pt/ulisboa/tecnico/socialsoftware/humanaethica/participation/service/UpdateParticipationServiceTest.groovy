package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
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

@DataJpaTest
class UpdateParticipationServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def activity
    def participation
    def volunteer

    def setup() {
        def institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                THREE_DAYS_AGO,TWO_DAYS_AGO, ONE_DAY_AGO, null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        volunteer = createVolunteer(USER_1_NAME,USER_1_PASSWORD,USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        participation = createParticipation(activity,volunteer,5)

    }

    def 'update participation' () {
        given:
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.rating = 1

        when:
        def result = participationService.updateParticipation(participation.id, updatedParticipationDto)

        then: "the returned data is correct"
        result.rating == 1
        and: "the participation is stored"
        participationRepository.findAll().size() == 1
        and: "contains the correct data"
        def storedParticipation = participationRepository.findAll().get(0)
        storedParticipation.rating == 1
        storedParticipation.acceptanceDate.isBefore(LocalDateTime.now())
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id

    }

    @Unroll
    def 'invalid arguments: rating=#rating | participationId=#participationId'() {
        given:
        def updatedParticipationDto = new ParticipationDto()
        updatedParticipationDto.rating = rating


        when:
        participationService.updateParticipation(getParticipationId(participationId), updatedParticipationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "the participation is in the database"
        participationRepository.findAll().size() == 1

        where:
        rating          | participationId   || errorMessage
        null            | EXIST             || ErrorMessage.PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE
        -2              | EXIST             || ErrorMessage.PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE
        3               | null              || ErrorMessage.PARTICIPATION_NOT_FOUND
        3               | NO_EXIST          || ErrorMessage.PARTICIPATION_NOT_FOUND
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
