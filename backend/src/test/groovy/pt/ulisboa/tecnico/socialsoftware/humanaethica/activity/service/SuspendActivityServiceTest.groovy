package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import spock.lang.Unroll

@DataJpaTest
class SuspendActivityServiceTest extends SpockTest {
    def activity
    def member

    def setup() {
        def institution = institutionService.getDemoInstitution()
        member = authUserService.loginDemoMemberAuth().getUser()

        given: "activity info"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1,Theme.State.APPROVED,null))
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "report activity with success"() {
        given:
        activity.setState(state)

        when:
        def result = activityService.suspendActivity(activity.id, member.id, ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)

        then: "the activity and theme are validated"
        result.state == Activity.State.SUSPENDED.name()

        where:
        state << [Activity.State.APPROVED, Activity.State.REPORTED]
    }

    @Unroll
    def "arguments: activityId=#activityId"() {
        when:
        activityService.suspendActivity(activityId, member.id, ACTIVITY_SUSPENSION_JUSTIFICATION_VALID)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND

        where:
        activityId << [null, 222]
    }

    @Unroll
    def "suspend activity with an invalid justification:#justification"() {
        when:
        activityService.suspendActivity(activity.id, member.id, justification)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        justification           || errorMessage
        null                    || ErrorMessage.ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID
        "too short"             || ErrorMessage.ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID
        generateLongString()    || ErrorMessage.ACTIVITY_SUSPENSION_JUSTIFICATION_INVALID
    }

    def generateLongString(){
        return 'a'* 257
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}