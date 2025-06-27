package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

@DataJpaTest
class CreateVolunteerProfileMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Participation participation1 = Mock()
    ParticipationDto participationDto1
    Participation participation2 = Mock()
    ParticipationDto participationDto2
    Participation participation3 = Mock()
    ParticipationDto participationDto3
    Participation participation4 = Mock()
    ParticipationDto participationDto4
    def volunteerProfileDto

    def setup() {
        given: "DTOs for participations"
        participation1.getId() >> 1
        participation2.getId() >> 2
        participation3.getId() >> 3
        participation4.getId() >> 4
        participationDto1 = new ParticipationDto()
        participationDto1.id = participation1.getId()
        participationDto2 = new ParticipationDto()
        participationDto2.id = participation2.getId()
        participationDto3 = new ParticipationDto()
        participationDto3.id = participation3.getId()
        participationDto4 = new ParticipationDto()
        participationDto4.id = participation4.getId()

        and: "a volunteer with enrollments"
        Enrollment e1 = Mock()
        Enrollment e2 = Mock()
        Enrollment e3 = Mock()
        Enrollment e4 = Mock()
        Enrollment e5 = Mock()
        volunteer.getEnrollments() >> [e1, e2, e3, e4, e5]
        and: "two assessments"
        Assessment a1 = Mock()
        Assessment a2 = Mock()
        volunteer.getAssessments() >> [a1, a2]
        and: "average rating of 4.75"
        participation1.getMemberRating() >> 5
        participation2.getMemberRating() >> 5
        participation3.getMemberRating() >> 5
        participation4.getMemberRating() >> 4
        and: "a valid volunteer profile info"
        volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.shortBio = VOLUNTEER_SHORT_BIO
        volunteerProfileDto.selectedParticipations = [participationDto1, participationDto2]
    }

    def "create volunteer profile"() {
        given: "three assessed participations"
        participation1.getMemberReview() >> ASSESSMENT_REVIEW_1
        participation2.getMemberReview() >> ASSESSMENT_REVIEW_1
        participation3.getMemberReview() >> ASSESSMENT_REVIEW_1
        and: "the following volunteer participations"
        volunteer.getParticipations() >> [participation1,
                                          participation2,
                                          participation3,
                                          participation4]
        when:
        def result = new VolunteerProfile(volunteer, volunteerProfileDto)

        then: "checks results"
        result.getShortBio() == VOLUNTEER_SHORT_BIO
        result.getNumTotalEnrollments() == 5
        result.getNumTotalParticipations() == 4
        result.getNumTotalAssessments() == 2
        result.getAverageRating() == 4.75
        result.getSelectedParticipations() == [participation1, participation2]
        and: "check that the volunteer profile is associated with the volunteer and the participations"
        1 * volunteer.setVolunteerProfile(_)
        1 * participation1.setVolunteerProfile(_)
        1 * participation2.setVolunteerProfile(_)
    }

    @Unroll
    def "create volunteer profile and violate shortBio invariant: shortBio=#shortBio"() {
        given:
        volunteerProfileDto.shortBio = shortBio
        and:
        volunteer.getParticipations() >> []

        when:
        new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        shortBio        || errorMessage
        null            || ErrorMessage.VOLUNTEER_PROFILE_SHORT_BIO_TOO_SHORT
        "        "      || ErrorMessage.VOLUNTEER_PROFILE_SHORT_BIO_TOO_SHORT
        "123456789"     || ErrorMessage.VOLUNTEER_PROFILE_SHORT_BIO_TOO_SHORT

    }

    def "create volunteer profile with at least one participations that is not assessed"() {
        given: "a list of participations where only one is assessed"
        participation2.getMemberReview() >> ASSESSMENT_REVIEW_1
        volunteer.getParticipations() >> [participation1, participation2]

        when:
        new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_PROFILE_SELECTED_PARTICIPATIONS_ASSESSED
    }

    def "create volunteer and violate minimum number of selected participations"() {
        given: "three assessed participations"
        participation1.getMemberReview() >> ASSESSMENT_REVIEW_1
        participation2.getMemberReview() >> ASSESSMENT_REVIEW_1
        participation3.getMemberReview() >> ASSESSMENT_REVIEW_1
        and: "a total of 4 volunteer participations"
        volunteer.getParticipations() >> [participation1,
                                          participation2,
                                          participation3,
                                          participation4]
        and: "the following selected participations"
        volunteerProfileDto.selectedParticipations = [participationDto1]

        when:
        new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_PROFILE_SELECTED_PARTICIPATIONS_MINIMUM
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}