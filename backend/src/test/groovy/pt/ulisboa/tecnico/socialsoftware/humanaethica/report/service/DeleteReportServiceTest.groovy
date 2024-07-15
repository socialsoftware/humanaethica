package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll


@DataJpaTest
class DeleteReportServiceTest extends SpockTest {
    public static final String FIRST_REPORT = 'REPORT_1'
    public static final String SECOND_REPORT = 'REPORT_2'

    def volunteer
    def activity
    def report
    def firstReport
    def secondReport

    def setup() {
        def institution = institutionService.getDemoInstitution()

        given: "activity info"
        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 2, ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS, IN_THREE_DAYS, null)
        
        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and: "report"
        report = createReport(activity, volunteer, REPORT_JUSTIFICATION_1)
    }

    def 'delete report'() {
        given:
        firstReport = reportRepository.findAll().get(0)
        when:
        reportService.removeReport(firstReport.id)
        then: "check that report was deleted"
        reportRepository.findAll().size() == 0

    }

    @Unroll
    def 'two reports exist and one is removed'() {
        
        given:
        def volunteer2 = createVolunteer(USER_2_NAME, USER_2_PASSWORD, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def report2 = createReport(activity, volunteer2, REPORT_JUSTIFICATION_2)
        reportRepository.save(report2)
        firstReport = reportRepository.findAll().get(0)
        secondReport = reportRepository.findAll().get(1)

        when:
        def result = reportService.removeReport(getFirstOrSecondReport(reportId))
    
        then: "the report was deleted"
        reportRepository.findAll().size() == 1
        result.justification == removedJustification 
        def remainingReport = reportRepository.findAll().get(0)
        remainingReport.justification == remainingJustification

        where: "check the justification of the remainingReport and of the removedReport"
        reportId        || removedJustification    || remainingJustification
        FIRST_REPORT    || REPORT_JUSTIFICATION_1  || REPORT_JUSTIFICATION_2
        SECOND_REPORT   || REPORT_JUSTIFICATION_2  || REPORT_JUSTIFICATION_1

    }

    def 'two reports exist and are both deleted'() {
        given:
        def volunteer2 = createVolunteer(USER_2_NAME, USER_2_PASSWORD, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        def report2 = createReport(activity, volunteer2, REPORT_JUSTIFICATION_2)
        reportRepository.save(report2)
        firstReport = reportRepository.findAll().get(0)
        secondReport = reportRepository.findAll().get(1)

        when:
        reportService.removeReport(firstReport.id)
        reportService.removeReport(secondReport.id)

        then: "confirm that reports were removed"
        reportRepository.findAll().size() == 0
    }

    @Unroll
    def 'invalid arguments: reportId=#reportId'() {

        when:
        reportService.removeReport(reportId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "the report is in the database"
        reportRepository.findAll().size() == 1

        where:
        reportId   ||  errorMessage
        null       ||  ErrorMessage.REPORT_NOT_FOUND
        222        ||  ErrorMessage.REPORT_NOT_FOUND

    }

    def getFirstOrSecondReport(reportId) {
    if(reportId == FIRST_REPORT)
        return firstReport.id
    else if (reportId == SECOND_REPORT)
        return secondReport.id
    return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}