package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.RegisterActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@DataJpaTest
class RegisterActivityTest extends SpockTest {
    def activityDto
    def theme
    def activity

    def "the activity does not exist, create the activity"() {
        given: "an activity dto"
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        activityDto = new RegisterActivityDto()
        activityDto.setActivityName("ACTIVITY_1_NAME")
        activityDto.setActivityRegion("ACTIVITY_1_REGION")
        activityDto.setActivityTheme("THEME_1_NAME")

        when:
        def result = activityService.registerActivity(activityDto)

        then: "the activity is saved in the database"
        activityRepository.findAll().size() == 1
        and: "checks if user data is correct"
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.getTheme().getName() == "THEME_1_NAME"

    }

    def "the activity and the theme does not exist, create the activity"() {
        given: "an activity dto"
        activityDto = new RegisterActivityDto()
        activityDto.setActivityName("ACTIVITY_1_NAME")
        activityDto.setActivityRegion("ACTIVITY_1_REGION")
        activityDto.setActivityTheme("THEME_1_NAME")

        when:
        def result = activityService.registerActivity(activityDto)

        then: "the activity and the theme are saved in the database"
        activityRepository.findAll().size() == 1
        themeRepository.findAll().size() == 1
        and: "checks if activity data is correct"
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.getTheme().getName() == "THEME_1_NAME"

    }

    def "the activity exists"() {
        given:
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", theme)
        activityRepository.save(activity)
        and:
        activityDto = new RegisterActivityDto()
        activityDto.setActivityName("ACTIVITY_1_NAME")
        activityDto.setActivityRegion("ACTIVITY_1_REGION")
        activityDto.setActivityTheme("THEME_1_NAME")

        when:
        activityService.registerActivity(activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_EXISTS
        and:
        activityRepository.count() == 1
    }

    @Unroll
    def "invalid arguments: name=#name | region=#region | themeName=#themeName"() {
        given: "an activity dto"
        activityDto = new RegisterActivityDto()
        activityDto.setActivityName(name)
        activityDto.setActivityRegion(region)
        activityDto.setActivityTheme(themeName)

        when:
        activityService.registerActivity(activityDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no activity was created"
        activityRepository.count() == 0

        where:
        name               | region               | themeName           || errorMessage
        null               | "ACTIVITY_1_REGION"  | "ACTIVITY_1_THEME"  || ErrorMessage.INVALID_ACTIVITY_NAME
        ""                 | "ACTIVITY_1_REGION"  | "ACTIVITY_1_THEME"  || ErrorMessage.INVALID_ACTIVITY_NAME
        "ACTIVITY_1_NAME"  | null                 | "ACTIVITY_1_THEME"  || ErrorMessage.INVALID_REGION_NAME
        "ACTIVITY_1_NAME"  | ""                   | "ACTIVITY_1_THEME"  || ErrorMessage.INVALID_REGION_NAME
        "ACTIVITY_1_NAME"  | "ACTIVITY_1_REGION"  | null                || ErrorMessage.INVALID_THEME_NAME
        "ACTIVITY_1_NAME"  | "ACTIVITY_1_REGION"  | ""                  || ErrorMessage.INVALID_THEME_NAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}