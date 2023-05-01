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
    def volunteer
    def theme
    def institution
    def activity

    def "create empty activity"() {
        when: "empty theme and empty activity are created"
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        activity = new Activity("","",theme)
        theme.addActivity(activity)
        activityRepository.save(activity)

        then: "checks if activity is saved"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getId() != 0
        result.getTheme().getId() == theme.getId()
        result.getVolunteers().size() == 0

        and: "the theme has a reference for the activity"
        theme.getActivities().size() == 1
        theme.getActivities().contains(result)
    }

    def "create activity and persists"() {
        given: "a institution, a theme and a volunteer"
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)

        when: "activity is created"
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", theme)
        activity.addVolunteer(volunteer)
        theme.addActivity(activity)
        activityRepository.save(activity)

        then: "activity is saved"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)

        and: "checks if activity data is correct"
        result.getId() != 0
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.isActive()
        result.getTheme().getName() == "THEME_1_NAME"
        result.getVolunteers().size() == 1
        result.getVolunteers().get(0).getName() == USER_1_NAME
        result.getVolunteers().get(0).getUsername() == USER_1_USERNAME
        result.getVolunteers().get(0).getEmail() == USER_1_EMAIL
        result.getVolunteers().get(0).getRole() == User.Role.VOLUNTEER

        and: "the theme has a reference for the activity"
        theme.getActivities().size() == 1
        theme.getActivities().contains(result)
    }

    def "remove a voluntary from activity"() {
        given: "a institution, a theme, a volunteer and a activity"
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", theme)
        activity.addVolunteer(volunteer)
        activityRepository.save(activity)

        when: "the voluntary is removed"
        activity.getVolunteers().size() == 1
        activity.removeVolunteer(volunteer.getId())

        then: "the voluntary is no longer persisted in the activity"
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)
        result.getVolunteers().size == 0
    }

    def "remove activity"() {
        given: "a institution, a theme, a volunteer and a activity"
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", theme)
        activity.addVolunteer(volunteer)
        theme.addActivity(activity)
        activityRepository.save(activity)

        when: "the activity is removed"
        activity.delete()
        theme.removeActivity(activity.getId())
        activity.removeVolunteer(volunteer.getId())
        activityRepository.delete(activity)

        then: "the activity is no longer persisted"
        activityRepository.count() == 0L
        volunteer.getActivity() == null
        theme.getActivities().size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}