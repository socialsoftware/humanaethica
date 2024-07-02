package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.report.dto.ReportDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

@DataJpaTest
class CreateReportServiceTest extends SpockTest {
    public static final String EXIST = 'exist'
    public static final String NO_EXIST = 'noExist'
    def volunteer
    def activity

    def setup() {
        def institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)
    }

    def 'create report' () {
        given:
        def reportDto = new ReportDto()
        reportDto.justification = REPORT_JUSTIFICATION_1

        when:
        def result = reportService.createReport(volunteer.id, activity.id, reportDto)

        then:
        result.justification == REPORT_JUSTIFICATION_1
        and:
        reportRepository.findAll().size() == 1
        def storedReport = reportRepository.findAll().get(0)
        storedReport.justification == REPORT_JUSTIFICATION_1
        storedReport.activity.id == activity.id
        storedReport.volunteer.id == volunteer.id
    }

    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | activityId=#activityId'() {
        given:
        def reportDto = new ReportDto()
        reportDto.justification = REPORT_JUSTIFICATION_1

        when:
        reportService.createReport(getVolunteerId(volunteerId), getActivityId(activityId), getReportDto(reportValue,reportDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and:
        reportRepository.findAll().size() == 0

        where:
        volunteerId | activityId | reportValue     || errorMessage
        null        | EXIST      | EXIST           || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST      | EXIST           || ErrorMessage.USER_NOT_FOUND
        EXIST       | null       | EXIST           || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | NO_EXIST   | EXIST           || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | EXIST      | null            || ErrorMessage.REPORT_REQUIRES_JUSTIFICATION
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

    def getReportDto(value, reportDto) {
        if (value == EXIST) {
            return reportDto
        }
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
