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
class UpdateVolunteerRatingParticipationMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Participation otherParticipation = Mock()
    def participation
    def participationDto
    def participationDtoUpdated


    def setup() {
        given:

        activity.getParticipations() >> [otherParticipation]
        activity.getNumberOfParticipatingVolunteers() >> 2
        activity.getApplicationDeadline() >> TWO_DAYS_AGO
        activity.getEndingDate() >> ONE_DAY_AGO
        activity.getParticipantsNumberLimit() >> 3

        participationDto = new ParticipationDto()
        participationDto.volunteerRating = 4
        participationDto.volunteerReview = MEMBER_REVIEW
        participation = new Participation(activity, volunteer, participationDto)

        participationDtoUpdated = new ParticipationDto()
    }

    def "volunteer updates a participation"() {
        given:
        participationDtoUpdated.volunteerRating = 3
        participationDtoUpdated.volunteerReview = VOLUNTEER_REVIEW


        when:
        participation.volunteerRating(participationDtoUpdated)

        then: "checks results"
        participation.volunteerRating == 3
        participation.volunteerReview == VOLUNTEER_REVIEW
        participation.acceptanceDate.isBefore(LocalDateTime.now())
        participation.activity == activity
        participation.volunteer == volunteer
    }

    @Unroll
    def "update participation and violate rating in range 1..5: rating=#rating"(){
        given:
        participationDtoUpdated.volunteerRating = rating
        participationDtoUpdated.volunteerReview = VOLUNTEER_REVIEW

        when:
        participation.volunteerRating(participationDtoUpdated)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE

        where:
        rating << [-5,0,6,20]
    }

    @Unroll
    def "update participation and violate review length: review=#review"(){
        given:
        participationDtoUpdated.volunteerRating = 5
        participationDtoUpdated.volunteerReview = review

        when:
        participation.volunteerRating(participationDtoUpdated)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_REVIEW_LENGTH_INVALID

        where:
        review << ["", "123456789","a".repeat(MAX_REVIEW_LENGTH + 1)]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}