package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateReportMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Volunteer otherVolunteer = Mock()
    Report otherReport = Mock()
    def reportDto

    def setup() {
        given: "report info"
        reportDto = new ReportDto()
        reportDto.justification = REPORT_JUSTIFICATION_1
    }

    def "create report"() {
        given:
        activity.getReports() >> [otherReport]
        activity.getEndingDate() >> IN_ONE_DAY
        otherReport.getVolunteer() >> otherVolunteer

        when:
        def result = new Report(activity, volunteer, reportDto)

        then: "checks results"
        result.justification == REPORT_JUSTIFICATION_1
        result.reportDateTime.isBefore(LocalDateTime.now())
        result.activity == activity
        result.volunteer == volunteer
        and: "check that it is added"
        1 * activity.addReport(_)
        1 * volunteer.addReport(_)
    }

    @Unroll
    def "create report and violate justification is required invariant: justfication=#justification"() {
        given:
        activity.getReports() >> [otherReport]
        activity.getEndingDate() >> IN_ONE_DAY
        otherReport.getVolunteer() >> otherVolunteer
        and:
        reportDto.justification = justification

        when:
        new Report(activity, volunteer, reportDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        justification           || errorMessage
        null                    || ErrorMessage.REPORT_REQUIRES_JUSTIFICATION
        generateLongString()    || ErrorMessage.REPORT_REQUIRES_JUSTIFICATION
    }

    def "create report and violate report before deadline invariant"() {
        given:
        activity.getReports() >> [otherReport]
        activity.getEndingDate() >> ONE_DAY_AGO
        otherReport.getVolunteer() >> otherVolunteer
        and:
        reportDto.justification = REPORT_JUSTIFICATION_1

        when:
        new Report(activity, volunteer, reportDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.REPORT_AFTER_ACTIVTY_CLOSED
    }

    def "create report and violate enroll once invariant"() {
        given:
        activity.getReports() >> [otherReport]
        activity.getApplicationDeadline() >> IN_ONE_DAY
        otherReport.getVolunteer() >> volunteer
        and:
        reportDto.justification = REPORT_JUSTIFICATION_1

        when:
        new Report(activity, volunteer, reportDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.REPORT_ACTIVTIY_IS_ALREADY_REPORTED
    }

    def generateLongString(){
        return 'a'* 257
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}