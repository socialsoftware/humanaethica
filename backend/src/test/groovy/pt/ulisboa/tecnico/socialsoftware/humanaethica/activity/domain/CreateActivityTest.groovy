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
class CreateActivityTest extends SpockTest {
    def volunteer
    def theme
    def institution
    def activity

    def "create Activity and persists"() {
        given:
        institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)
        theme = new Theme("THEME_1_NAME")
        themeRepository.save(theme)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)

        when:
        activity = new Activity("ACTIVITY_1_NAME", "ACTIVITY_1_REGION", theme)
        activity.addVolunteer(volunteer)
        activityRepository.save(activity)

        then:
        activityRepository.count() == 1L
        def result = activityRepository.findAll().get(0)

        and: "checks if activity data is correct"
        result.getName() == "ACTIVITY_1_NAME"
        result.getRegion() == "ACTIVITY_1_REGION"
        result.isActive()
        result.getTheme().getName() == "THEME_1_NAME"
        result.getVolunteers().size() == 1
        result.getVolunteers().get(0).getName() == USER_1_NAME
        result.getVolunteers().get(0).getUsername() == USER_1_USERNAME
        result.getVolunteers().get(0).getEmail() == USER_1_EMAIL
        result.getVolunteers().get(0).getRole() == User.Role.VOLUNTEER
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}