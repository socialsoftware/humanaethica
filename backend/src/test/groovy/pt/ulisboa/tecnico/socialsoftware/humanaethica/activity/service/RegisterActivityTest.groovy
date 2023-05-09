package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import spock.lang.Unroll


@DataJpaTest
class RegisterActivityTest extends SpockTest {
    def activityDto
    def themeDto
    def theme
    def activity

    def "the activity does not exist, create the activity"() {
        given: "an activity dto"
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()
        themes.add(theme)

        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", Activity.State.APPROVED)
        activity.setThemes(themes)
        activityDto = new ActivityDto(activity)

        when:
        def result = activityService.registerActivity(activityDto)

        then: "the activity is saved in the database"
        activityRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.getThemes().get(0).getName() == "THEME_1_NAME"
        result.getState() == Activity.State.APPROVED

    }

    def "the activity already exists"() {
        given:
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", Activity.State.APPROVED)
        activity.addTheme(theme)
        activityRepository.save(activity)
        and:
        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")

        when:
        activityService.registerActivity(activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
        and:
        activityRepository.count() == 1
    }

    def "add theme to an activity"() {
        given:
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", Activity.State.APPROVED)
        activityDto = new ActivityDto(activity)
        def result = activityService.registerActivity(activityDto)

        when:
        result.getThemes().size() == 0
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()
        themes.add(theme)
        activityService.addTheme(result.getId(), themes)

        then: "the activity is associated with the theme"
        result.getThemes().size() == 1
        result.getThemes().get(0).getName() == "THEME_1_NAME"
    }

    @Unroll
    def "invalid arguments: name=#name | region=#region"() {
        given: "an activity dto"
        activityDto = new ActivityDto()
        activityDto.setName(name)
        activityDto.setRegion(region)

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