package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import spock.lang.Unroll


@DataJpaTest
class RegisterActivityTest extends SpockTest {
    def activityDto
    def theme
    def activity

    def "the activity does not exist, create the activity"() {
        given: "an activity dto"
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()
        themes.add(theme)
        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setThemes(themes)

        when:
        def result = activityService.registerActivity(activityDto)

        then: "the activity is saved in the database"
        activityRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.getThemes().get(0).getName() == "THEME_1_NAME"
        result.getState() == Activity.State.SUBMITTED

    }

    def "the activity exists"() {
        given:
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", themes, Activity.State.SUBMITTED)
        activity.addTheme(theme)
        activityRepository.save(activity)
        and:
        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setThemes(themes)

        when:
        activityService.registerActivity(activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
        and:
        activityRepository.count() == 1
    }

    @Unroll
    def "invalid arguments: name=#name | region=#region"() {
        given: "an activity dto"
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)
        activityDto.setThemes(themes)

        when:
        activityService.registerActivity(activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity was created"
        activityRepository.count() == 0

        where:
        name               | region               || errorMessage
        null               | "ACTIVITY_1_REGION"  || ErrorMessage.INVALID_ACTIVITY_NAME
        ""                 | "ACTIVITY_1_REGION"  || ErrorMessage.INVALID_ACTIVITY_NAME
        "ACTIVITY_1_NAME"  | null                 || ErrorMessage.INVALID_REGION_NAME
        "ACTIVITY_1_NAME"  | ""                   || ErrorMessage.INVALID_REGION_NAME
  }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}