package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.report.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State

@DataJpaTest
class GetReportsByActivityServiceTest extends SpockTest {
    def activity
    def otherActivity

    def setup() {
        def institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(activity)

        activityDto.name = ACTIVITY_NAME_2
        otherActivity = new Activity(activityDto, institution, new ArrayList<>())
        activityRepository.save(otherActivity)
    }

    def "get two reports of the same activity"() {
        given:
        def volunteerOne = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, State.APPROVED)
        def volunteerTwo = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, State.APPROVED)
        and:
        createReport(activity, volunteerOne, REPORT_JUSTIFICATION_1)
        createReport(activity, volunteerTwo, REPORT_JUSTIFICATION_2)

        when:
        def reports = reportService.getReportsByActivity(activity.id)

        then:
        reports.size() == 2
        reports.get(0).justification == REPORT_JUSTIFICATION_1
        reports.get(1).justification == REPORT_JUSTIFICATION_2
    }

    def "get one report of an activity"() {
        given:
        def volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, State.APPROVED)
        and:
        createReport(activity, volunteer, REPORT_JUSTIFICATION_1)
        createReport(otherActivity, volunteer, REPORT_JUSTIFICATION_2)

        when:
        def reports = reportService.getReportsByActivity(activity.id)

        then:
        reports.size() == 1
        reports.get(0).justification == REPORT_JUSTIFICATION_1
    }

    def "activity does not exist or is null: activityId=#activityId"() {
        when:
        reportService.getReportsByActivity(activityId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        activityId || errorMessage
        null       || ErrorMessage.ACTIVITY_NOT_FOUND
        222        || ErrorMessage.ACTIVITY_NOT_FOUND
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
