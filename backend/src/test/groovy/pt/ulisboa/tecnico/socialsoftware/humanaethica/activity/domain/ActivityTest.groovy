package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution

@DataJpaTest
class ActivityTest extends SpockTest {
    def theme
    def institution
    def activity

    def "create empty activity"() {
        when: "empty activity are created"
        institution = new Institution()
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()
        activity = new Activity("", "", "", institution, "", "", "" ,Activity.State.APPROVED)
        activity.addTheme(theme)
        activityRepository.save(activity)

        then: "checks if activity is saved"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getId() != 0
        result.getThemes().get(0).getId() == theme.getId()

        and: "the theme has a reference for the activity"
        theme.getActivities().size() == 1
        theme.getActivities().contains(result)
    }

    def "create activity and persists"() {
        given: "a theme and a volunteer"
        institution = new Institution()
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(theme)
        List<Theme> themes = new ArrayList<>()

        when: "activity is created"
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", "ACTIVITY_1_DESCRIPTION", institution, "", "", "", Activity.State.APPROVED)
        activityRepository.save(activity)
        activity.addTheme(theme)


        then: "activity is saved"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)

        and: "checks if activity data is correct"
        result.getId() != 0
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.getDescription() == "ACTIVITY_1_DESCRIPTION"
        result.getThemes().get(0).getName() == "THEME_1_NAME"

        and: "the theme has a reference for the activity"
        theme.getActivities().size() == 1
        theme.getActivities().contains(result)
    }

    def "remove a theme from activity"() {
        given: "a theme and a activity"
        institution = new Institution()
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(theme)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", "ACTIVITY_1_DESCRIPTION", institution, "", "", "",Activity.State.APPROVED)
        activity.addTheme(theme)
        activityRepository.save(activity)

        when: "the voluntary and theme are removed"
        activity.removeTheme(theme)

        then: "the voluntary and theme are no longer persisted in the activity"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getThemes().size() == 0
    }

    def "suspend activity"() {
        given: "a volunteer and a activity"
        institution = new Institution()
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        List<Theme> themes = new ArrayList<>()
        themeRepository.save(theme)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", "ACTIVITY_1_DESCRIPTION", institution, "", "", "", Activity.State.APPROVED)
        activity.addTheme(theme)
        activityRepository.save(activity)

        when: "the activity is suspended"
        activity.suspend()

        then: "the activity is no longer persisted"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getState() == Activity.State.SUSPENDED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}