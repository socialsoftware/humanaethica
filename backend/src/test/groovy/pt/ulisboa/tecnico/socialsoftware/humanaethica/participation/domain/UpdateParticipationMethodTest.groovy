package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

import java.time.LocalDateTime


@DataJpaTest
class UpdateParticipationMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Participation otherParticipation = Mock()
    def participation
    def participationDto
    def participationDtoUpdated

    def setup() {
        given:
        participationDto = new ParticipationDto()
        participationDto.rating = 4
        activity.getParticipations() >> [otherParticipation]
        activity.getNumberOfParticipatingVolunteers() >> 2
        activity.getApplicationDeadline() >> TWO_DAYS_AGO
        activity.getEndingDate() >> ONE_DAY_AGO
        activity.getParticipantsNumberLimit() >> 3
        participation = new Participation(activity, volunteer, participationDto)
        participationDtoUpdated = new ParticipationDto()
    }

    def "update participation"() {
        given:
        participationDtoUpdated.rating = 3

        when:
        participation.update(participationDtoUpdated)

        then: "checks results"
        participation.rating == 3
        participation.acceptanceDate.isBefore(LocalDateTime.now())
        participation.activity == activity
        participation.volunteer == volunteer

    }


    @Unroll
    def "update participation and violate rating in range 1..5: rating=#rating"(){
        given:
        participationDtoUpdated.rating = rating

        when:
        participation.update(participationDtoUpdated)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE

        where:
        rating << [-5,0,6,20]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}