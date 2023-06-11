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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity

@DataJpaTest
class ActivityTest extends SpockTest {
    def volunteer
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
        activity = new Activity("","","",institution,Activity.State.APPROVED)
        activity.addTheme(theme)
        activityRepository.save(activity)

        then: "checks if activity is saved"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getId() != 0
        result.getThemes().get(0).getId() == theme.getId()
        result.getVolunteers().size() == 0

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
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)

        when: "activity is created"
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", "ACTIVITY_1_DESCRIPTION", institution, Activity.State.APPROVED)
        activityRepository.save(activity)
        activity.addVolunteer(volunteer)
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
        result.getVolunteers().size() == 1
        result.getVolunteers().get(0).getName() == USER_1_NAME
        result.getVolunteers().get(0).getUsername() == USER_1_USERNAME
        result.getVolunteers().get(0).getEmail() == USER_1_EMAIL
        result.getVolunteers().get(0).getRole() == User.Role.VOLUNTEER

        and: "the theme has a reference for the activity"
        theme.getActivities().size() == 1
        theme.getActivities().contains(result)
    }

    def "remove a voluntary and a theme from activity"() {
        given: "a theme, a volunteer and a activity"
        institution = new Institution()
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        themeRepository.save(theme)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", "ACTIVITY_1_DESCRIPTION", institution, Activity.State.APPROVED)
        activity.addVolunteer(volunteer)
        activity.addTheme(theme)
        activityRepository.save(activity)

        when: "the voluntary and theme are removed"
        activity.getVolunteers().size() == 1
        activity.removeVolunteer(volunteer.getId())
        activity.removeTheme(theme.getId())

        then: "the voluntary and theme are no longer persisted in the activity"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getVolunteers().size() == 0
        result.getThemes().size() == 0
    }

    def "suspend activity"() {
        given: "a theme, a volunteer and a activity"
        institution = new Institution()
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME", Theme.State.APPROVED, null)
        List<Theme> themes = new ArrayList<>()
        themeRepository.save(theme)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", "ACTIVITY_1_DESCRIPTION", institution, Activity.State.APPROVED)
        activity.addVolunteer(volunteer)
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