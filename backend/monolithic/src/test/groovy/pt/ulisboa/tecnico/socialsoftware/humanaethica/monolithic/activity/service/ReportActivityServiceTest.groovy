package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.theme.domain.Theme
import spock.lang.Unroll

@DataJpaTest
class ReportActivityServiceTest extends SpockTest {
    def activity

    def setup() {
        def institution = institutionService.getDemoInstitution()
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
        def result = activityService.reportActivity(activity.id)

        then: "the activity and theme are validated"
        result.state == Activity.State.REPORTED.name()

        where:
        state << [Activity.State.APPROVED, Activity.State.SUSPENDED]
    }

    @Unroll
    def "arguments: activityId=#activityId"() {
        when:
        activityService.reportActivity(activityId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND

        where:
        activityId << [null, 222]
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}