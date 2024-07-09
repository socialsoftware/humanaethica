package pt.ulisboa.tecnico.socialsoftware.humanaethica.report.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User


@DataJpaTest
class GetVolunteerReportsServiceTest extends SpockTest {
    def activityOne
    def activityTwo

    def setup(){
        def institution = institutionService.getDemoInstitution()

        def activityDtoOne = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS,IN_THREE_DAYS,null)

        activityOne = new Activity(activityDtoOne, institution, new ArrayList<>())
        activityRepository.save(activityOne)

        activityDtoOne.name = ACTIVITY_NAME_2

        activityTwo = new Activity(activityDtoOne, institution, new ArrayList<>())
        activityRepository.save(activityTwo)
    }

    def "get two reports of the same volunteer"() {
        given: 
        def volunteerOne = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createReport(activityOne, volunteerOne, REPORT_JUSTIFICATION_1)
        createReport(activityTwo, volunteerOne, REPORT_JUSTIFICATION_2)

        when:
        def reports = reportService.getVolunteerReports(volunteerOne.id)

        then:
        reports.size() == 2
        reports.get(0).justification == REPORT_JUSTIFICATION_1
        reports.get(1).justification == REPORT_JUSTIFICATION_2
    }

    def "get one report of a volunteer"() {
        given: 
        def volunteerOne = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        and:
        createReport(activityOne, volunteerOne, REPORT_JUSTIFICATION_1)

        when:
        def reports = reportService.getVolunteerReports(volunteerOne.id)

        then:
        reports.size() == 1
        reports.get(0).justification == REPORT_JUSTIFICATION_1
    }

    def "volunteer does not exist or is null: volunteerId=#volunteerId"() {
        when:
        reportService.getVolunteerReports(volunteerId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        volunteerId || errorMessage
        null        || ErrorMessage.USER_NOT_FOUND
        222         || ErrorMessage.USER_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
