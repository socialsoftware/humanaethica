package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.RegisterActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import spock.lang.Unroll


@DataJpaTest
class ValidateActivityTest extends SpockTest {
    def activityDto
    def themeDto
    def activity
    def theme

    def setup() {
        activityDto = new RegisterActivityDto()
        activityDto.setActivityName("ACTIVITY_1_NAME")
        activityDto.setActivityRegion("ACTIVITY_1_REGION")
        activityDto.setActivityTheme("THEME_1_NAME")

        activity = activityService.registerActivity(activityDto)

        themeDto = new ThemeDto()
        themeDto.setName("THEME_1_NAME")

        theme = themeService.registerTheme(themeDto);
    }

    def "validate activity with success"() {
        when:
        //themeService.validateTheme(theme.getId())
        activityService.validateActivity(activity.getId())

        then: "the activity and theme are validated"
        activity.isActive()
   }

    def "the activity doesn't exist"() {
        when:
        activityService.validateActivity(activity.getId() + 1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    /*def "the theme haven't yet been approved"() {
        when:
        activityService.validateActivity(activity.getId())
        theme.setState(false)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_NOT_APPROVED
    }*/

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}