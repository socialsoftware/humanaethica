package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.InstitutionProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

@DataJpaTest
class CreateInstitutionProfileMethodTest extends SpockTest {
    Volunteer volunteer = Mock()

    Institution institution = Mock()
    Assessment assessment1 = Mock()
    AssessmentDto assessmentDto1
    Assessment assessment2 = Mock()
    AssessmentDto assessmentDto2
    Assessment assessment3 = Mock()
    AssessmentDto assessmentDto3
    Assessment assessment4 = Mock()
    AssessmentDto assessmentDto4
    Assessment assessment5 = Mock()
    AssessmentDto assessmentDto5

    def institutionProfileDto

    def setup() {
        given: "an institution with 4 members"
        institution.getMembers() >> (1..4).collect { Mock(Member) }

        and: "3 activities with 4 participating volunteers that rank them"
        Activity a1 = Mock()
        Participation p1a1 = Mock()
        p1a1.getVolunteerRating() >> 5
        a1.getParticipations() >> [p1a1]
        a1.getNumberOfParticipatingVolunteers() >> 1
        Activity a2 = Mock()
        Participation p1a2 = Mock()
        p1a2.getVolunteerRating() >> 5
        Participation p2a2 = Mock()
        p2a2.getVolunteerRating() >> 4
        a2.getParticipations() >> [p1a2, p2a2]
        a2.getNumberOfParticipatingVolunteers() >> 2
        Activity a3 = Mock()
        Participation p1a3 = Mock()
        p1a3.getVolunteerRating() >> 5
        Participation p2a3 = Mock()
        p2a3.getVolunteerRating() >> 5
        a3.getParticipations() >> [p1a3, p2a3]
        a3.getNumberOfParticipatingVolunteers() >> 2
        institution.getActivities() >> [a1, a2, a3]

        and: "five assessments with review dates"
        assessment1.getId() >> 1
        assessment1.getReviewDate() >> NOW
        assessment2.getId() >> 2
        assessment2.getReviewDate() >> ONE_DAY_AGO
        assessment3.getId() >> 3
        assessment3.getReviewDate() >> TWO_DAYS_AGO
        assessment4.getId() >> 4
        assessment4.getReviewDate() >> THREE_DAYS_AGO
        assessment5.getId() >> 5
        assessment5.getReviewDate() >> THREE_DAYS_AGO

        and: "an institution with 5 assessments"
        institution.getAssessments() >> [assessment1, assessment2, assessment3, assessment4, assessment5]

        and: "DTOs for assessments"
        assessmentDto1 = new AssessmentDto()
        assessmentDto1.id = assessment1.getId()
        assessmentDto2 = new AssessmentDto()
        assessmentDto2.id = assessment2.getId()
        assessmentDto3 = new AssessmentDto()
        assessmentDto3.id = assessment3.getId()
        assessmentDto4 = new AssessmentDto()
        assessmentDto4.id = assessment4.getId()
        assessmentDto5 = new AssessmentDto()
        assessmentDto5.id = assessment5.getId()

        and: "an example an incomplete institution profile DTO"
        institutionProfileDto = new InstitutionProfileDto()
    }

    def "create institution profile"() {
        given: "a valid DTO with correctly selected assessments"
        institutionProfileDto.shortDescription = INSTITUTION_SHORT_DESC
        institutionProfileDto.selectedAssessments = [assessmentDto1, assessmentDto3, assessmentDto4]
        when:
        def result = new InstitutionProfile(institution, institutionProfileDto)

        then: "checks results"
        result.getShortDescription() == INSTITUTION_SHORT_DESC
        result.getNumMembers() == 4
        result.getNumActivities() == 3
        result.getNumAssessments() == 5
        result.getNumVolunteers() == 5
        result.getAverageRating() == 4.8
        result.getSelectedAssessments() == [assessment1, assessment3, assessment4]
        and: "check that the institution profile is associated with the institution and the assessments"
        1 * institution.setInstitutionProfile(_)
        1 * assessment1.setInstitutionProfile(_)
        1 * assessment3.setInstitutionProfile(_)
        1 * assessment4.setInstitutionProfile(_)
    }

    @Unroll
    def "create institution profile and violate shortDescription invariant: shortDescription=#shortDescription"() {
        given: "a DTO with an invalid short description"
        institutionProfileDto.shortDescription = shortDescription
        institutionProfileDto.selectedAssessments = [assessmentDto1, assessmentDto3, assessmentDto4]

        when:
        new InstitutionProfile(institution, institutionProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        shortDescription        || errorMessage
        null                    || ErrorMessage.INSTITUTION_PROFILE_SHORT_DESC_TOO_SHORT
        "        "              || ErrorMessage.INSTITUTION_PROFILE_SHORT_DESC_TOO_SHORT
        "123456789"             || ErrorMessage.INSTITUTION_PROFILE_SHORT_DESC_TOO_SHORT
    }

    def "create institution profile and violate invariant about recent assessments"() {
        given: "a DTO with a valid short description"
        institutionProfileDto.shortDescription = INSTITUTION_SHORT_DESC
        and: "a selection of DTOs missing 20% of the most recent ones"
        institutionProfileDto.selectedAssessments = [assessmentDto2, assessmentDto3, assessmentDto4, assessmentDto5]

        when:
        new InstitutionProfile(institution, institutionProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_PROFILE_NOT_ENOUGH_RECENT_ASSESSMENTS
    }

    @Unroll
    def "create institution profile and violate invariant about minimum number of assessments: numSelectedAssessments: #numSelectedAssessments / 5"() {
        given: "a DTO with a valid short description"
        institutionProfileDto.shortDescription = INSTITUTION_SHORT_DESC
        and: "a selection of DTOs not meeting the minimum number (50%)"
        institutionProfileDto.selectedAssessments = [assessmentDto1, assessmentDto2, assessmentDto3, assessmentDto4, assessmentDto5].subList(0,numSelectedAssessments)

        when:
        new InstitutionProfile(institution, institutionProfileDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_PROFILE_NOT_ENOUGH_ASSESSMENTS

        where:
        numSelectedAssessments << [1, 2]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}