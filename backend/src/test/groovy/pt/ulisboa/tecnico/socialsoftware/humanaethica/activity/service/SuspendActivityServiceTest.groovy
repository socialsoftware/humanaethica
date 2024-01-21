package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class SuspendActivityServiceTest extends SpockTest {
    def activity

    def setup() {
        def member = authUserService.loginDemoMemberAuth().getUser()
        def institution = institutionService.getDemoInstitution()
        given: "activity info"
        def activityDto = new ActivityDto()
        activityDto.name = ACTIVITY_NAME_1
        activityDto.region = ACTIVITY_REGION_1
        activityDto.participantsNumber = 1
        activityDto.description = ACTIVITY_DESCRIPTION_1
        activityDto.startingDate = DateHandler.toISOString(IN_TWO_DAYS)
        activityDto.endingDate = DateHandler.toISOString(IN_THREE_DAYS)
        activityDto.applicationDeadline = DateHandler.toISOString(IN_ONE_DAY)
        and: "a theme"
        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED, null)
        themeRepository.save(theme)
        def themes = new ArrayList<>()
        themes.add(theme)
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "report activity with success"() {
        given:
        activity.setState(state)

        when:
        def result = activityService.suspendActivity(activity.id)

        then: "the activity and theme are validated"
        result.state == Activity.State.SUSPENDED.name()

        where:
        state << [Activity.State.APPROVED, Activity.State.REPORTED]
    }

    @Unroll
    def "arguments: activityId=#activityId"() {
        when:
        activityService.suspendActivity(activityId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND

        where:
        activityId << [null, 222]
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}