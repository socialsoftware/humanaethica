package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration

import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity


@DataJpaTest
class ValidateActivityTest extends SpockTest {
    def activityDto
    def theme
    def activity
    def newActivity

    def setup() {
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED)
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()
        themes.add(theme)

        newActivity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", Activity.State.REPORTED)
        newActivity.setThemes(themes)
        activityDto = new ActivityDto(newActivity)

        activity = activityService.registerActivity(activityDto)
        activityService.reportActivity(activity.getId())
    }

    def "validate activity with success"() {
        when:
        activityService.validateActivity(activity.getId())

        then: "the activity and theme are validated"
        activity.getState() == Activity.State.APPROVED
   }

    def "the activity doesn't exist"() {
        when:
        activityService.validateActivity(activity.getId() + 1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def "the activity is already approved"() {
        when:
        activityService.validateActivity(activity.getId())
        activityService.validateActivity(activity.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_APPROVED
    }

    /*def "the theme haven't yet been approved"() {
        when:
        theme.setState(Theme.State.SUBMITTED)
        themes.add(theme)
        activity.setThemes(themes)
        activityService.validateActivity(activity.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_NOT_APPROVED
    }*/

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}